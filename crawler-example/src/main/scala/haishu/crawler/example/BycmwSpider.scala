package haishu.crawler.example

import java.io.IOException
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import haishu.crawler.{Response, SimpleJob}

import scala.concurrent.duration._
import scala.collection.JavaConverters._

class BycmwSpider extends SimpleJob {

  val name = "bycmw"

  val startUrls =
    (1 to 20).map(i => s"http://www.bycmw.com/news/newslist-0-4-aa-p$i.html") ++
      (1 to 20).map(i => s"http://www.bycmw.com/news/newslist-0-1-aa-p$i.html") ++
      (1 to 20).map(i => s"http://www.bycmw.com/news/newslist-0-50-aa-p$i.html")

  override val downloadTimeout = 10.seconds

  override def parse(response: Response) = {

    val urls = response.css(".ListMain > div > ul").links().all()

    val requests = urls.map(
      url => response
        .follow(url, parseArticle)
        .extra("source", "巴渝传媒网")
        .errback(logErrBack(url)(Paths.get(s"err_urls_$name")))
    )

    collectRequests(requests)

  }

  def logErrBack(url: String)(path: Path): Throwable => Unit = {
    case e: IOException =>
      Files.write(path, List(url).asJava, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
    case _ =>
  }

  def parseArticle(response: Response) = {
    val title = response.css(".Title_h1 > h1", "text").head()
    println(title)
    result(Map("title" -> title))
  }

}
