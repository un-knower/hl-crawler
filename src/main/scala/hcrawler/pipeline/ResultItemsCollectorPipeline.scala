package hcrawler.pipeline

import hcrawler.{ResultItems, Task}

/**
  * Created by hldev on 7/25/17.
  */
class ResultItemsCollectorPipeline extends CollectorPipeline[ResultItems] {

  private var collector: Seq[ResultItems] = Vector[ResultItems]()

  override def process(resultItems: ResultItems, task: Task) = {
    collector :+= resultItems

  }

  override def collected = collector

}

object ResultItemsCollectorPipeline {
  def apply(): ResultItemsCollectorPipeline = new ResultItemsCollectorPipeline()
}
