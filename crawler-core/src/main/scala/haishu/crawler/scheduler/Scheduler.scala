package haishu.crawler.scheduler

import haishu.crawler.{Request, Task}

trait Scheduler {

  def push(request: Request, task: Task): Unit

  def poll(task: Task): Request

}
