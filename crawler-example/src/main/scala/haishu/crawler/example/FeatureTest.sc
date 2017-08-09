import haishu.crawler.{OkHttpDownloader, Request}
import okhttp3.OkHttpClient

val client = new OkHttpClient()

val url = "http://fsd.sdf"

OkHttpDownloader.download(client, Request(url, PartialFunction.empty))