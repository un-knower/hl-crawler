package hcrawler
package downloader

trait Downloader {

  def download(request: Request, task: Task): Page

  def setThread(nThreads: Int): Unit

}