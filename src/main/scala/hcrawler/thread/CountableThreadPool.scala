package hcrawler.thread

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.{ExecutorService, Executors}

/**
  * Created by hldev on 7/25/17.
  */
class CountableThreadPool(
    nThreads: Int,
    executorService: ExecutorService) {

  private val _threadAlive = new AtomicInteger(0)

  private val reentrantLock = new ReentrantLock()

  private val condition =  reentrantLock.newCondition()

  def threadAlive = _threadAlive.get()

  def execute(runnable: Runnable): Unit = {

    if (threadAlive >= nThreads) {
      try {
        reentrantLock.lock()
        while (threadAlive >= nThreads) {
          try {
            condition.await()
          } catch {
            case e: InterruptedException =>
          }
        }
      } finally {
        reentrantLock.unlock()
      }
    }

    _threadAlive.incrementAndGet()
    executorService.execute(new Runnable {
      override def run() = {
        try {
          runnable.run()
        } finally {
          try {
            reentrantLock.lock()
            _threadAlive.decrementAndGet()
            condition.signal()
          } finally {
            reentrantLock.unlock()
          }
        }
      }
    })

  }

  def isShutdown: Boolean = executorService.isShutdown

  def shutdown(): Unit = executorService.shutdown()

}

object CountableThreadPool {

  def apply(nThreads: Int, executorService: ExecutorService): CountableThreadPool =
    new CountableThreadPool(nThreads, executorService)

  def apply(nThreads: Int): CountableThreadPool = new CountableThreadPool(nThreads, Executors.newFixedThreadPool(nThreads))

}