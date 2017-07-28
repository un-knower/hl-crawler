package haishu.crawler.example

import haishu.crawler.pipeline.ConsolePipeline
import haishu.crawler.{Page, Site, Spider}
import haishu.crawler.processor.PageProcessor
import haishu.crawler.selector.Selectable

object StatsZxfbExample extends App {

  trait Formatter[T] {
    def format(s: String): T
  }

  implicit val string2Int = new Formatter[Int] {
    def format(s: String) = s.toInt
  }

  implicit class HlStringFormatter(s: String) {
    def as[T: Formatter]: T = implicitly[Formatter[T]].format(s)
  }

  implicit class SelectableFormatter(s: Selectable) {
    def as[T: Formatter] = s.get().get.as[T]
  }

  case class Article(title: String, content: String)

  case class Person(name: String, age: Int)

  case class Foo(x1: Int, x2: String, x3: Double, x4: Byte)

  class ZxfbPageProcessor extends PageProcessor {

    val site = Site("http://www.stats.gov.cn").sleepTime(100).cycleRetryTimes(2)

    override def process(p: Page) = {

      p.follow(p.css(".center_list").links().regex(""".*\d{8}_\d{7}.html$"""))

      val title = p.css(".xilan_tite", "text")

      val content = p.css(".TRS_Editor")

      if (!title.isMatch) p.skip()

      p.put("title", title)
      p.put("source", p.css("font[style=color:#1f5781;margin-right:50px;]", "text"))
      p.put("content", p.css(".TRS_Editor"))

    }

  }

  val urls = "http://www.stats.gov.cn/tjsj/sjjd/index.html" +:
    (1 to 44).map(i => s"http://www.stats.gov.cn/tjsj/sjjd/index_$i.html")

  val url = "http://www.stats.gov.cn/tjsj/zxfb/"

  val url2 = Seq("http://gitlab.hualongdata.com/", url)

  Spider(new ZxfbPageProcessor)
    .thread(40)
    .startUrls(url2 ++ urls)
    .run()
}
