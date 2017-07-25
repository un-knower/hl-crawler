import hcrawler.{Page, Site}
import hcrawler.processor.PageProcessor

/**
  * Created by hldev on 7/25/17.
  */
object GithubRepoExample {

  val site = Site("https://github.com").sleepTime(1000).timeOut(10000)

  /*class GithubRepoPageProcessor extends PageProcessor {
    override def process(page: Page) = {
      page.addTargetRequests(page.html.css("span .d-block").links().all())

    }
  }*/

}
