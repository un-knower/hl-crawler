package haishu.crawler.pipeline

import haishu.crawler.{ResultItems, Task}

trait Pipeline {

  def process(resultItems: ResultItems, task: Task): Unit

}
