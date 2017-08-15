package haishu.crawler.example

import haishu.crawler.Crawler
import haishu.crawler.pipeline.SingleFilePipeline
import haishu.crawler._

import scala.io.StdIn

object StatsZxfbExample extends App {

  case class Article(title: String, content: String)

  class ZxfbJob extends SimpleJob {

    val name = "zxfb"

    val startUrls = Seq(
      "http://www.stats.gov.cn/tjsj/zxfb/",
    )

    override val retryTimes = 3

    override val pipelines = Seq(SingleFilePipeline("/home/hldev/Shen/zxfb"))

    def parse(r: Response) = {

      val links = r.css(".center_list").links().regex(""".*\d{8}_\d{7}.html$""").all().map(r.follow(_, parseItem))

      collectRequests(links)

    }

    def parseItem(r: Response) = {
      val article = for {
        title <- r.css(".xilan_tit", "text").headOption()
        content <- r.css(".TRS_Editor").headOption()
      } yield Map("title" -> title, "content" -> content)
      article match {
        case Some(m) => result(m)
        case None =>
          println(r.body.length)
          throw new Exception(s"${r.url} parse error")
      }
    }

  }

  val urls = "http://www.stats.gov.cn/tjsj/sjjd/index.html" +:
    (1 to 44).map(i => s"http://www.stats.gov.cn/tjsj/sjjd/index_$i.html")

  val url = "http://www.stats.gov.cn/tjsj/zxfb/"

  val url2 = Seq("http://gitlab.hualongdata.com/", url)

  Crawler.submit(new ZxfbJob())

  StdIn.readLine("Press enter to exit...\n")

  println("System terminate...")

  Crawler.terminate()

}
