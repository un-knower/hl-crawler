import haishu.crawler.selector.Html
import okhttp3.{OkHttpClient, Request}
import org.jsoup.Jsoup
import collection.JavaConverters._

val url = "http://www.stats.gov.cn/tjsj/zxfb/"

val doc = Jsoup.connect(url).execute().parse()

doc.select(".center_list > ul > li > span")

val html = new Html(doc)

html.css(".center_list").links().regex(""".*\d{8}_\d{7}.html$""").all()

val url1 = "http://cq.spb.gov.cn/zcfg/index.html"

val doc1 = Jsoup.connect(url1).execute().parse()

doc1.select("#submenu111").select("a").first().attr("abs:href")