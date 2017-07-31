package haishu.crawler.example

import java.time.{Instant, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.util.{Date, Locale}

import haishu.crawler.pipeline.ConsolePipeline
import haishu.crawler.{Page, Site, Spider}
import haishu.crawler.processor.PageProcessor
import haishu.crawler.selector.Selectable

object StatsZxfbExample extends App {

  trait Formatter[T] {
    def format(s: String): T
  }

  def gen[T](body: String => T) = new Formatter[T] {
    override def format(s: String) = body(s)
  }

  implicit val string2String = gen[String](identity)

  implicit val string2Double = gen[Double](_.toDouble)

  implicit val string2Int = gen[Int](_.toInt)

  implicit val string2Long = gen[Long](_.toLong)

  implicit val string2Date = gen[Date] { s =>
    val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.CHINA).withZone(ZoneId.systemDefault())
    val zdt = ZonedDateTime.from(timeFormatter.parse(s))
    Date.from(zdt.toInstant)
  }

  implicit class HlStringFormatter(s: String) {
    def as[T: Formatter]: T = implicitly[Formatter[T]].format(s)
  }

  implicit class SelectableFormatter(s: Selectable) {
    def as[T: Formatter] = s.get().get.as[T]
  }

  def collect[T1: Formatter, T2: Formatter, T3: Formatter, P](constructor: (T1, T2, T3) => P, field1: Selectable, field2: Selectable, field3: Selectable): P =
    constructor(
      field1.as[T1],
      field2.as[T2],
      field3.as[T3]
    )

  case class Article(title: String, content: String, publishedAt: Date)

  class ZxfbPageProcessor extends PageProcessor {

    val site = Site("http://www.stats.gov.cn").sleepTime(200).cycleRetryTimes(2)

    override def process(p: Page) = {

      p.follow(p.css(".center_list").links().regex(""".*\d{8}_\d{7}.html$"""))

      val title = p.css(".xilan_tit", "text")

      val content = p.css(".TRS_Editor")

      val pulishedAt = p.css("font[style=float:left;width:620px;text-align:right;margin-right:60px;]").regex("""(?s)发布时间：(.*)</font>""", 1)
      println(pulishedAt.get())
      if (!title.isMatch) p.skip()
      else {
        val a = collect(
          Article,
          title,
          content,
          pulishedAt
        )
        println(a)
      }

    }

  }

  val urls = "http://www.stats.gov.cn/tjsj/sjjd/index.html" +:
    (1 to 44).map(i => s"http://www.stats.gov.cn/tjsj/sjjd/index_$i.html")

  val url = "http://www.stats.gov.cn/tjsj/zxfb/"

  val url2 = Seq("http://gitlab.hualongdata.com/", url)

  Spider(new ZxfbPageProcessor)
    .thread(40)
    .startUrls(url)
    .run()
}
