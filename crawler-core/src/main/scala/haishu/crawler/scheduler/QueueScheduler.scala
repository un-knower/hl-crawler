package haishu.crawler.scheduler

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

import haishu.crawler.{Request, Task}

class QueueScheduler extends DuplicateRemovedScheduler {

  private val queue: BlockingQueue[Request] = new LinkedBlockingQueue[Request]()

  override def pushWhenNoDuplicate(request: Request, task: Task): Unit = {
    queue.add(request)
  }

  override def poll(task: Task): Request = {
    queue.poll()
  }

}

object QueueScheduler {
  def apply(): QueueScheduler = new QueueScheduler()
}
