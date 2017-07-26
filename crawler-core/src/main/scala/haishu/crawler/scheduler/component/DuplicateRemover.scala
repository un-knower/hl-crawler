package haishu.crawler.scheduler.component

import haishu.crawler.{Request, Task}

trait DuplicateRemover {

  def isDuplicate(request: Request, task: Task): Boolean

  def resetDuplicateCheck(task: Task): Unit

  def totalRequestsCount(task: Task): Int

}
