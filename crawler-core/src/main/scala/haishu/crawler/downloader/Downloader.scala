package haishu.crawler.downloader

import haishu.crawler.{Page, Request, Task}

trait Downloader {

  def download(request: Request, task: Task): Page

}
