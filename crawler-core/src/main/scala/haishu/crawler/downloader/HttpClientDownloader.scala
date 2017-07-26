package haishu.crawler.downloader

import java.io.IOException
import java.net.CookieHandler

import com.typesafe.scalalogging.Logger
import haishu.crawler.selector.PlainText
import haishu.crawler.util.CharsetUtils
import haishu.crawler.{Page, Request, Task}
import okhttp3.{JavaNetCookieJar, OkHttpClient, Response}

import scala.collection.JavaConverters._

/**
 * Created by hldev on 7/24/17.
 */
class HttpClientDownloader extends AbstractDownloader {

  private val log = Logger("HttpClientDownloader")

  private val httpClient = new OkHttpClient()

  // TODO: proxy
  override def download(request: Request, task: Task): Page = {
    val okRequest = OkRequestConverter.convert(request, task.site)
    var page = Page.fail()
    try {
      val response = httpClient.newCall(okRequest).execute()
      page = handleResponse(request, task.site.charset, response, task)
      log.info(s"downloading page success ${request.url}")
      page
    } catch {
      case e: IOException =>
        log.warn(s"download page ${request.url} error", e)
        page
    } finally {
      // TODO: ensure connection is released back to pool
    }
  }

  protected def handleResponse(request: Request, charset: String, response: Response, task: Task): Page = {
    val content = getResponseContent(charset, response)
    var headers = Map[String, Seq[String]]()
    for (n <- response.headers().names().asScala) {
      headers += (n -> response.headers(n).asScala)
    }
    val page = Page(
      request = request,
      rawText = content,
      url = PlainText(request.url),
      statusCode = response.code(),
      downloadSuccess = true,
      headers = headers)
    page
  }

  private def getResponseContent(charset: String, response: Response) = {
    val contentBytes = response.body().bytes()
    val htmlCharset = getHtmlCharset(response, contentBytes)
    if (htmlCharset.nonEmpty) new String(contentBytes, htmlCharset.get)
    else {
      log.warn(s"Charset autodetect failed, use $charset as charset")
      new String(contentBytes, charset)
    }

  }

  private def getHtmlCharset(response: Response, contentBytes: Array[Byte]): Option[String] = {
    val charset = response.body().contentType().charset()
    if (charset == null) CharsetUtils.detectCharset(contentBytes)
    else Some(charset.toString)
  }
}

object HttpClientDownloader {
  def apply(): HttpClientDownloader = new HttpClientDownloader()
}
