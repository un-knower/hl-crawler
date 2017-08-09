package haishu.crawler.pipeline

import haishu.crawler.Item

trait Pipeline {

  def process(item: Item): Option[Item]

  def onOpen(): Unit = ()

  def onClose(): Unit = ()
}

class ConsolePipeline extends Pipeline {

  def process(item: Item) = {
    println(item.toString)
    Some(item)
  }

}
