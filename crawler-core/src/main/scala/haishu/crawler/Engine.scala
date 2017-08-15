package haishu.crawler

import akka.actor.Status.Failure
import akka.actor.{Actor, Cancellable, Props}
import haishu.crawler.Messages._
import okhttp3.OkHttpClient
import pipeline.{ItemPipeline, Pipeline}

import scala.concurrent.duration._

object Engine {

  def props(pipelines: Seq[Pipeline])(implicit client: OkHttpClient) = Props(new Engine(pipelines))

}

class Engine(pipelines: Seq[Pipeline])(implicit client: OkHttpClient) extends Actor {

  import context.system

  import context.dispatcher

  val log = system.log

  val scheduler = context.actorOf(Scheduler.props(self), "scheduler")

  val spider = context.actorOf(Spider.props, "spider")

  val downloader = context.actorOf(OkHttpDownloader.props(client), "downloader")

  val itemPipelines = pipelines.map { p =>
    p.onOpen()
    context.actorOf(ItemPipeline.props(p), p.toString)
  }

  var timer: Cancellable = _

  var noRequestTimes = 0

  var downloadSuccess = 0

  var downloadFailAfterRetry = 0

  override def preStart() = {
    timer = system.scheduler.schedule(200.millis, Config.intervalBetweenRequest, scheduler, PollRequest)
  }

  override def postStop() = {
    pipelines.foreach(_.onClose())
    timer.cancel()
    log.info(s"Job ${self.path.name} complete. $downloadSuccess succeed and $downloadFailAfterRetry fail")
  }

  def receive = {
    case ScheduleRequest(request) =>
      scheduler ! request
    case RetryRequest(request) =>
      log.info(s"download retry ${request.url}")
      scheduler ! request.retry

    case ReplyRequest(request) =>
      noRequestTimes = 0
      downloader ! Download(request)
    case NoRequest =>
      noRequestTimes += 1
      if (noRequestTimes >= Config.noRequestTimes) {
        val pool = client.connectionPool()
        if (pool.connectionCount() == pool.idleConnectionCount()) context.stop(self)
        else {
          noRequestTimes -= 10
        }
      }

    case r: Response =>
      spider ! ParseResponse(r)
      downloadSuccess += 1

    case ProcessItem(item) =>
      itemPipelines.headOption.foreach(_ ! ProcessItem(item))
    case ProcessItemNext(item) =>
      val nextIndex = itemPipelines.indexOf(sender()) + 1
      if (nextIndex < pipelines.length) itemPipelines(nextIndex) ! ProcessItem(item)

    case Failure(e) =>
      downloadFailAfterRetry += 1
  }

}
