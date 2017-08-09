package haishu.crawler.example

import haishu.crawler.{Main, _}
import haishu.crawler.pipeline.JsonPipeline

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object StatsZxfbExample extends App {

  case class Article(title: String, content: String)

  class ZxfbJob extends SimpleJob {

    val name = "zxfb"

    val startUrls = Seq(
      "http://www.stats.gov.cn/tjsj/zxfb/"
    )

    override val pipelines = Seq(JsonPipeline("/home/hldev/test.txt"))

    def parse(r: Response) = {

      val links = r.css(".center_list").links().regex(""".*\d{8}_\d{7}.html$""").all().map(r.follow(_, parseItem))

      collectRequests(links)

    }

    def parseItem(r: Response) = {
      val article = for {
        title <- r.css(".xilan_tit", "text").headOption()
        content <- r.css(".TRS_Editor").headOption()
      } yield Map("title" -> title, "content" -> content)
      result(article.get)
    }

  }

  val urls = "http://www.stats.gov.cn/tjsj/sjjd/index.html" +:
    (1 to 44).map(i => s"http://www.stats.gov.cn/tjsj/sjjd/index_$i.html")

  val url = "http://www.stats.gov.cn/tjsj/zxfb/"

  val url2 = Seq("http://gitlab.hualongdata.com/", url)

  Main.submit(new ZxfbJob())

  StdIn.readLine("Press enter to exit...\n")

  println("System terminate...")

  Main.system.terminate().foreach { _ =>
    Main.client.dispatcher().executorService().shutdown()
    Main.client.connectionPool().evictAll()
  }

}
