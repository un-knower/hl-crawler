package hcrawler

import java.util.Date
import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

import com.typesafe.scalalogging.Logger
import hcrawler.downloader.Downloader
import hcrawler.downloader.HttpClientDownloader
import hcrawler.pipeline.Pipeline
import hcrawler.processor.PageProcessor
import hcrawler.scheduler.{QueueScheduler, Scheduler}
import java.util.concurrent.{ExecutorService, Executors}
import javafx.scene.chart.PieChart

import scala.concurrent.ExecutionContext
/**
  * Created by hldev on 7/24/17.
  */
case class Spider(
    pageProcessor: PageProcessor,
    downloader: Downloader = HttpClientDownloader(),
    pipelines: Seq[Pipeline] = Vector(),
    startRequests: Seq[Request] = Vector(),
    scheduler: Scheduler = QueueScheduler()
    ) {

  import Spider._

  private val log = Logger("Spider")

  private val _site = pageProcessor.site

  private var _uuid = _site.domain

  private var executorService: ExecutorService = _

  def uuid(uuid: String) = {
    _uuid = uuid
    this
  }

  protected val stat = new AtomicReference[SpiderStat](Init)

  protected var exitWhenComplete = true

  protected var spawnUrl = true

  protected var destroyWhenExit = true

  private var emptySleepTime = 3000

  private val pageCount = new AtomicLong(0)

  private var _startTime: Date = _

  def startUrls(urls: Seq[String]): Spider = {
    val requests = urls.map(url => Request(url))
    copy(startRequests = startRequests ++ requests)
  }

  def startRequests(requests: Seq[Request]): Spider = {
    copy(startRequests = startRequests ++ requests)
  }

  def scheduler(s: Scheduler): Spider = copy(scheduler = s)

  def pipeline(p: Pipeline): Spider = pipelines(p)

  def pipelines(ps: Seq[Pipeline]): Spider = copy(pipelines = pipelines ++ ps)

  def pipelines(p: Pipeline, rest: Pipeline*): Spider = pipelines(p +: rest)

  def noPipeline(): Spider = copy(pipelines = Vector())

  def downloader(d: Downloader): Spider = copy(downloader = d)

  def init(): Unit = {
    if (executorService == null || executorService.isShutdown) {
      executorService = Executors.newScheduledThreadPool(Runtime.getRuntime.availableProcessors())
    }
  }

}

object Spider {
  sealed trait SpiderStat
  final case object Init extends SpiderStat
  final case object Running extends SpiderStat
  final case object Stopped extends SpiderStat
}