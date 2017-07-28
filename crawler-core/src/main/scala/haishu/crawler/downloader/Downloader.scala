package haishu.crawler.downloader

import haishu.crawler.selector.Html
import haishu.crawler.{Page, Request, Site, Task}

trait Downloader {

  def download(request: Request, task: Task): Page

  def download(url: String): Html = {
    val page = download(Request(url), Site().toTask)
    page.html
  }

  def download(url: String, charset: String): Html = {
    val page = download(Request(url), Site().charset(charset).toTask)
    page.html
  }

}
