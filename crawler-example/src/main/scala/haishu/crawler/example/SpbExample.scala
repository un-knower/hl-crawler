package haishu.crawler.example

import haishu.crawler.pipeline.ResultItemsCollectorPipeline
import haishu.crawler.processor.PageProcessor
import haishu.crawler.{Page, Site, Spider}

/**
 * Created by hldev on 7/25/17.
 */
object SpbExample extends App {

  val s = Site("http://cq.spb.gov.cn").sleepTime(100)

  class SpbPageProcessor extends PageProcessor {
    override def process(page: Page) = {
      page.addTargetRequests(page.html.css("#submenu111").links().all())

      page.putField("title", page.html.css(".flo.boxcenter > div", "text").get())
      page.putField("content", page.html.css(".dlll").get())

      if (page.resultItems.get("title").isEmpty) page.skip(true)

      println(page.resultItems.get[String]("title"))
    }

    override def site = s
  }

  val startUrls = Seq(
    "http://cq.spb.gov.cn/zcfg/index_1.html",
    "http://cq.spb.gov.cn/zcfg/index.html")

  val p = ResultItemsCollectorPipeline()

  Spider(new SpbPageProcessor).addUrls(startUrls).thread(50).run()

}
