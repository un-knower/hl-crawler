package hcrawler
package downloader

import selector.Html

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

abstract class AbstractDownloader extends Downloader {

  def download(url: String)(implicit ec: ExecutionContext): Future[Html] = {
    val page = download(Request(url), Site().toTask)
    page.map(_.html)
  }

  def download(url: String, charset: String)(implicit ec: ExecutionContext): Future[Html] = {
    val page = download(Request(url), Site().charset(charset).toTask)
    page.map(_.html)
  }

}