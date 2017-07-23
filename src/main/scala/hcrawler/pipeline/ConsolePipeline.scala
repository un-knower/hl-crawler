package hcrawler
package pipeline

class ConsolePipeline extends Pipeline {

  override def process(resultItems: ResultItems, task: Task): Unit = {
    println(s"get page: ${resultItems.request.url}")
    for ((key, value) <- resultItems.getAll) {
      println(s"$key:\t$value")
    }
  }
}