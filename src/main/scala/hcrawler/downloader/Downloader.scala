package hcrawler
package downloader

import scala.concurrent.Future

trait Downloader {

  def download(request: Request, task: Task): Page

}