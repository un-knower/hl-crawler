package haishu.crawler.downloader

import haishu.crawler.selector.Html
import haishu.crawler.{Request, Site}

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
