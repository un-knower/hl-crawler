package hcrawler
package scheduler

trait Scheduler {

  def push(request: Request, task: Task): Unit

  def poll(task: Task): Request

}