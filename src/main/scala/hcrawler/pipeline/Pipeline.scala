package hcrawler
package pipeline

trait Pipeline {

  def process(resultItems: ResultItems, task: Task): Unit

}