package haishu.crawler.scheduler.component

import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

import haishu.crawler.{Request, Task}

import collection.JavaConverters._

class HashSetDuplicateRemover extends DuplicateRemover {

  private val urls = Collections.newSetFromMap(new ConcurrentHashMap[String, java.lang.Boolean]()).asScala

  def isDuplicate(request: Request, task: Task): Boolean = {
    !(urls.add(request.url))
  }

  def resetDuplicateCheck(task: Task): Unit = {
    urls.clear()
  }

  def totalRequestsCount(task: Task): Int = {
    urls.size
  }
}
