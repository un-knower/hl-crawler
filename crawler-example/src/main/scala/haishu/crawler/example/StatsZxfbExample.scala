package haishu.crawler.example

import haishu.crawler.pipeline.ConsolePipeline
import haishu.crawler.{Page, Site, Spider}
import haishu.crawler.processor.PageProcessor

object StatsZxfbExample extends App {

  val s = Site("http://www.stats.gov.cn").sleepTime(100)

  class ZxfbPageProcessor extends PageProcessor {

    override def site: Site = s

    override def process(page: Page): Unit = {

      page.addTargetRequests(page.html.css(".center_list").links().all())

      page.resultItems.put("title", page.html.css(".xilan_tit", "text").get())
      page.resultItems.put("source", page.html.css("font[style=color:#1f5781;margin-right:50px;]", "text").get())
      page.resultItems.put("content", page.html.css(".TRS_Editor").get())

      if (page.resultItems.get("title").isEmpty) page.skip(true)

      println(page.resultItems.get("title"))

    }

  }

  val urls = "http://www.stats.gov.cn/tjsj/sjjd/index.html" +:
    (1 to 44).map(i => s"http://www.stats.gov.cn/tjsj/sjjd/index_$i.html")

  val url = "http://www.stats.gov.cn/tjsj/zxfb/"

  Spider(new ZxfbPageProcessor)
    .pipeline(ConsolePipeline())
    .thread(4)
    .startUrls(url)
    .run()
}
