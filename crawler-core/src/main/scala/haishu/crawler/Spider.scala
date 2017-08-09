package haishu.crawler

import akka.actor.{Actor, ActorRef, Props}
import haishu.crawler.Messages.{ParseResponse, ProcessItem, ScheduleRequest}

class Spider extends Actor {

  def receive = {
    case ParseResponse(response) =>
      val results = response.request.callback(response)
      results foreach {
        case Left(request) =>
          sender() ! ScheduleRequest(request)
        case Right(item) =>
          sender() ! ProcessItem(item)
      }
  }
}

object Spider {

  def props = Props(new Spider)

}
