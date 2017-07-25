package hcrawler
package downloader

import selector.Html

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

abstract class AbstractDownloader extends Downloader {

  def download(url: String): Html = {
    val page = download(Request(url), Site().toTask())
    page.html
  }

  def download(url: String, charset: String): Html = {
    val page = download(Request(url), Site().charset(charset).toTask())
    page.html
  }

}