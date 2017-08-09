package haishu.crawler

import akka.actor.{ActorRef, ActorSystem}
import haishu.crawler.Messages.ScheduleRequest
import okhttp3.OkHttpClient

import scala.util.Success

object Main {

  @volatile private var inited = false

  private[crawler] implicit var system: ActorSystem = _

  private[crawler] implicit var client: OkHttpClient = _

  private[crawler] def init() =
    if (inited) {

    } else {
      system = ActorSystem("crawler")
      client = new OkHttpClient()
      inited = true
    }

  def submit(job: Job): Unit = {
    init()
    val engine = system.actorOf(Engine.props(job.pipeliens), job.name)
    job.startRequests.foreach(r => engine ! ScheduleRequest(r))
  }

  def submit(j: SimpleJob): Unit = submit(j.build())

  def terminate() =
    if (inited) {
      client.dispatcher().executorService().shutdown()
      client.connectionPool().evictAll()
      system.terminate()
      inited = false
    } else {

    }

  def test(url: String) = {
    init()
    val response = OkHttpDownloader.download(client, Request(url, PartialFunction.empty))
    terminate()
    response
  }

}
