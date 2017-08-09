package haishu.crawler.pipeline

import akka.actor.{Actor, Props}
import haishu.crawler.Messages.{ProcessItem, ProcessItemNext}

class ItemPipeline(pipeline: Pipeline) extends Actor {

  def receive = {
    case ProcessItem(item) =>
      pipeline.process(item).foreach { result =>
        sender() ! ProcessItemNext(result)
      }
  }

}

object ItemPipeline {

  def props(pipeline: Pipeline) = Props(new ItemPipeline(pipeline))

}
