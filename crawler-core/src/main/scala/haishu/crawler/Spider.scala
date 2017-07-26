package haishu.crawler

import java.util.Date
import java.util.concurrent.atomic.{AtomicLong, AtomicReference}
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import com.typesafe.scalalogging.Logger
import haishu.crawler.downloader.{Downloader, HttpClientDownloader}
import haishu.crawler.pipeline.Pipeline
import haishu.crawler.processor.PageProcessor
import haishu.crawler.scheduler.{QueueScheduler, Scheduler}
import haishu.crawler.thread.CountableThreadPool

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal
/**
 * Created by hldev on 7/24/17.
 */
class Spider private (
    private var _pageProcessor: PageProcessor,
    private var _downloader: Downloader,
    private var _pipelines: Seq[Pipeline],
    private var _startRequests: Seq[Request],
    private var _scheduler: Scheduler,
    private var _nThreads: Int) extends Runnable with Task {

  import Spider._

  private val log = Logger("Spider")

  private val _site = _pageProcessor.site

  private var _uuid = _site.domain

  private var _executorService: ExecutorService = _

  private var _threadPool: CountableThreadPool = _

  def uuid(uuid: String): Spider = {
    _uuid = uuid
    this
  }

  override def uuid: String = _uuid

  protected val stat = new AtomicReference[SpiderStat](Init)

  protected var exitWhenComplete = true

  protected var spawnUrl = true

  protected var destroyWhenExit = true

  private var emptySleepTime = 3000

  private val pageCount = new AtomicLong(0)

  private var _startTime: Date = _

  private val newUrlLock = new ReentrantLock()

  private val newUrlCondition = newUrlLock.newCondition()

  def startUrls(urls: Seq[String]): Spider = {
    val requests = urls.map(url => Request(url))
    _startRequests = _startRequests ++ requests
    this
  }

  def startUrls(url: String, rest: String*): Spider = startUrls(url +: rest)

  def addUrls(url: String, rest: String*): Spider = addUrls(url +: rest)

  def addUrls(urls: Seq[String]): Spider = {
    urls.foreach(url => addRequest(Request(url)))
    signalNewUrl()
    this
  }

  def startRequests(requests: Seq[Request]): Spider = {
    _startRequests = _startRequests ++ requests
    this
  }

  def scheduler(s: Scheduler): Spider = {
    _scheduler = s
    this
  }

  def pipeline(p: Pipeline): Spider = pipelines(p)

  def pipelines(ps: Seq[Pipeline]): Spider = {
    _pipelines = _pipelines ++ ps
    this
  }

  def pipelines(p: Pipeline, rest: Pipeline*): Spider = pipelines(p +: rest)

  def noPipeline(): Spider = {
    _pipelines = Vector()
    this
  }

  def downloader(d: Downloader): Spider = {
    _downloader = d
    this
  }

  def thread(n: Int): Spider = {
    _nThreads = n
    this
  }

  def executorService(es: ExecutorService): Spider = {
    _executorService = es
    this
  }

  override def site: Site = _site

  def initComponents(): Unit = {
    if (_executorService == null || _executorService.isShutdown) {
      _executorService = Executors.newFixedThreadPool(_nThreads)
    }
    if (_threadPool == null || _threadPool.isShutdown) {
      _threadPool = CountableThreadPool(_nThreads, _executorService)
    }
    _startRequests.foreach(addRequest)
    _startRequests = Vector()
    _startTime = new Date()
  }

  protected def checkRunningStat(): Unit = {
    val statNow = stat.get()
    if (statNow == Running) throw new IllegalStateException("Spider is already running!")
    if (!stat.compareAndSet(statNow, Running)) checkRunningStat()
  }

  override def run(): Unit = {
    checkRunningStat()
    initComponents()
    log.info(s"Spider $uuid started!")
    var exitFlag = false
    while (!Thread.currentThread().isInterrupted && stat.get == Running && !exitFlag) {
      val request = _scheduler.poll(this)
      if (request == null) {
        if (_threadPool.threadAlive == 0 && exitWhenComplete) {
          exitFlag = true
        } else {
          waitNewUrl()
        }
      } else {
        _threadPool.execute(() => {
          try {
            processRequest(request)
            //onSuccess(request)
          } catch {
            case NonFatal(e) =>
              //onError(request )
              log.error("process request " + request + " error", e)
          } finally {
            pageCount.incrementAndGet()
            signalNewUrl()
          }
        })
      }
    }
    stat.set(Stopped)
    _threadPool.shutdown()
    log.info(s"Spider $uuid closed! ${pageCount.get} pages downloaded.")
  }

  def test(urls: String*): Unit = {
    initComponents()
    urls.foreach(url => processRequest(new Request(url)))
  }

  private def processRequest(request: Request) = {
    val page = _downloader.download(request, this)
    if (page.downloadSuccess) onDownloadSuccess(request, page)
    else onDownloadFail(request)
  }

  private def onDownloadSuccess(request: Request, page: Page) = {
    if (site.acceptStatCodes.contains(page.statusCode)) {
      _pageProcessor.process(page)
      extractAndAddRequests(page, spawnUrl)
      if (!page.resultItems.skip) {
        _pipelines.foreach(_.process(page.resultItems, this))
      }
    } else log.info(s"page status code error, page ${request.url} , code: ${page.statusCode}")
    sleep(site.sleepTime)
  }

  private def extractAndAddRequests(page: Page, spawnUrl: Boolean) = {
    if (spawnUrl && page.targetRequests.nonEmpty) {
      page.targetRequests.foreach(addRequest)
    }
  }

  private def sleep(time: Int) = {
    try {
      Thread.sleep(time)
    } catch {
      case e: InterruptedException => log.error("Thread interrupted when sleep", e)
    }
  }

  private def onDownloadFail(request: Request) = {

  }

  private def waitNewUrl(): Unit = {
    newUrlLock.lock()
    try {
      if (_threadPool.threadAlive == 0 && exitWhenComplete) return

      newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS)
    } catch {
      case e: InterruptedException =>
        log.warn(s"waitNewUrl - interrupted, error $e")
    } finally {
      newUrlLock.unlock()
    }
  }

  private def signalNewUrl() = {
    try {
      newUrlLock.lock()
      newUrlCondition.signalAll()
    } finally {
      newUrlLock.unlock()
    }
  }

  private def addRequest(request: Request) = {
    _scheduler.push(request, this)
  }

}

object Spider {
  sealed trait SpiderStat
  final case object Init extends SpiderStat
  final case object Running extends SpiderStat
  final case object Stopped extends SpiderStat

  def apply(
    pageProcessor: PageProcessor,
    downloader: Downloader = HttpClientDownloader(),
    pipelines: Seq[Pipeline] = Vector(),
    startRequests: Seq[Request] = Vector(),
    scheduler: Scheduler = QueueScheduler(),
    nThreads: Int = 1): Spider = {

    new Spider(
      pageProcessor,
      downloader,
      pipelines,
      startRequests,
      scheduler,
      nThreads)
  }

}
