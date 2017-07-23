package hcrawler
package scheduler
package component

trait DuplicateRemover {

  def isDuplicate(request: Request, task: Task): Boolean

  def resetDuplicateCheck(task: Task): Unit

  def totalRequestsCount(task: Task): Int

}