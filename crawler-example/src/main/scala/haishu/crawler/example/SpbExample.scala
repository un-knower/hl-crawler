/*
package haishu.crawler.example

import haishu.crawler.pipeline.ResultItemsCollectorPipeline
import haishu.crawler.processor.PageProcessor
import haishu.crawler.{Page, Site, Spider}

/**
 * Created by hldev on 7/25/17.
 */
object SpbExample extends App {

  class SpbPageProcessor extends PageProcessor {

    val site = Site("http://cq.spb.gov.cn").sleepTime(1000)

    override def process(p: Page) = {

      p.follow(p.css("#submenu111").links())

      val title = p.css(".flo.boxcenter > div", "text")

      if (!title.isMatch) p.skip()

      p.put("title", title)
      p.put("content", p.css(".dlll"))

      println(title.get())
    }

  }

  val startUrls = Seq(
    "http://cq.spb.gov.cn/zcfg/index_1.html",
    "http://cq.spb.gov.cn/zcfg/index.html")

  val p = ResultItemsCollectorPipeline()

  Spider(new SpbPageProcessor).addUrls(startUrls).thread(50).run()

}
*/
