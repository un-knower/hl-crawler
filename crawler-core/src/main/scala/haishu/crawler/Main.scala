package haishu.crawler

import akka.actor.{ActorRef, ActorSystem}
import haishu.crawler.Messages.ScheduleRequest
import okhttp3.OkHttpClient

object Main {

  implicit val system = ActorSystem("crawler")

  implicit val client = new OkHttpClient()

  def submit(job: Job): Unit = {
    val engine = system.actorOf(Engine.props(job.pipeliens), job.name)
    job.startRequests.foreach(r => engine ! ScheduleRequest(r))
  }

  def submit(j: SimpleJob): Unit = submit(j.build())

}
