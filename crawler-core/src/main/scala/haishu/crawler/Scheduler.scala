package haishu.crawler

import java.util.concurrent.{BlockingQueue, LinkedBlockingDeque, TimeUnit, TimeoutException}

import Messages.{NoRequest, PollRequest, ReplyRequest}
import akka.actor.{Actor, ActorRef, Props}

import scala.collection.mutable

object Scheduler {

  def props(engine: ActorRef) = Props(new Scheduler(engine))

}

class Scheduler(engine: ActorRef) extends BaseActor {

  val queue = new LinkedBlockingDeque[Request]()

  val seen = mutable.Set[String]()

  def poll() = queue.poll()

  override def receive = {
    case request: Request =>
      val url = request.url
      if (!seen.contains(url)) {
        log.debug(s"push to queue $url")
        seen += url
        queue.add(request)
      }
    case PollRequest =>
      val request = poll()
      val engine = sender()
      if (request != null) engine ! ReplyRequest(request)
      else engine ! NoRequest
  }
}
