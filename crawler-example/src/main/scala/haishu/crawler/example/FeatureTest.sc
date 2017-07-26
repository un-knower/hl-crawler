import haishu.crawler.selector.Html
import okhttp3.{OkHttpClient, Request}
import org.jsoup.Jsoup



val url = "http://cq.spb.gov.cn/zcfg/201603/t20160316_732411.html"

val doc = Jsoup.connect(url).execute().parse()

doc.select(".flo.boxcenter").text()

val html = new Html(doc)

html.css(".dlll").get()

val request = new Request.Builder().url(url).build()

val client = new OkHttpClient()

val response = client.newCall(request).execute()

response.body().contentType().charset().toString
