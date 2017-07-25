import hcrawler.{Page, Site, Spider}
import hcrawler.processor.PageProcessor
import hcrawler.pipeline.{ConsolePipeline, ResultItemsCollectorPipeline}

/**
  * Created by hldev on 7/25/17.
  */
object SpbExample extends App {

  val s = Site("http://cq.spb.gov.cn").sleepTime(100)

  class SpbPageProcessor extends PageProcessor {
    override def process(page: Page) = {
      page.addTargetRequests(page.html.css("#submenu111").links().all())

      page.putField("title", page.html.css(".flo .boxcenter").get())
      page.putField("content", page.html.css(".dlll"))

      if (page.resultItems.get("title") == null) page.skip(true)
    }

    override def site = s
  }

  val startUrls = Seq(
    "http://cq.spb.gov.cn/zcfg/index_1.html",
    "http://cq.spb.gov.cn/zcfg/index.html"
  )

  val p = ResultItemsCollectorPipeline()

  Spider(new SpbPageProcessor).addUrls(startUrls).thread(50).pipeline(p).run()

}
