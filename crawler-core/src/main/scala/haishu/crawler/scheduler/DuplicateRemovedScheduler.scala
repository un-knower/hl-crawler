package haishu.crawler.scheduler

import com.typesafe.scalalogging.Logger
import haishu.crawler.scheduler.component.{DuplicateRemover, HashSetDuplicateRemover}
import haishu.crawler.util.HttpConstant.Method
import haishu.crawler.{Request, Task}

abstract class DuplicateRemovedScheduler extends Scheduler {

  protected lazy val log = Logger("DuplicateRemovedScheduler")

  private var _duplicatedRemover: DuplicateRemover = new HashSetDuplicateRemover()

  def duplicateRemover: DuplicateRemover = _duplicatedRemover

  def duplicateRemover(dr: DuplicateRemover) = {
    _duplicatedRemover = dr
  }

  override def push(request: Request, task: Task): Unit = {
    log.trace(s"get a candidate url ${request.url}")
    if (noNeedToRemoveDuplicate(request) || !duplicateRemover.isDuplicate(request, task)) {
      log.debug(s"push to queue ${request.url}")
      pushWhenNoDuplicate(request, task)
    }
  }

  protected def noNeedToRemoveDuplicate(request: Request): Boolean = {
    Method.POST.equalsIgnoreCase(request.method)
  }

  protected def pushWhenNoDuplicate(request: Request, task: Task): Unit = {

  }
}
