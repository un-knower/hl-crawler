package haishu.crawler

import akka.actor.{Actor, ActorSystem}
import akka.event.LoggingAdapter

import scala.concurrent.ExecutionContextExecutor

trait BaseActor extends Actor {
  implicit val system: ActorSystem = context.system
  implicit val dispatcher: ExecutionContextExecutor = context.dispatcher
  val log: LoggingAdapter = system.log
}
