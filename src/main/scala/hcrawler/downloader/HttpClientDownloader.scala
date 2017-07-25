package hcrawler
package downloader

import java.io.IOException
import java.nio.charset.Charset

import com.typesafe.scalalogging.Logger
import hcrawler.selector.PlainText
import okhttp3.{Call, Callback, OkHttpClient, Response}

import scala.concurrent.{Future, Promise}

import collection.JavaConverters._

/**
  * Created by hldev on 7/24/17.
  */
class HttpClientDownloader extends AbstractDownloader {

  private val log = Logger("HttpClientDownloader")

  private val httpClient = new OkHttpClient()

  override def download(request: Request, task: Task): Future[Page] = {
    val okRequest = OkRequestConverter.convert(request, task.site)
    val promise = Promise[Page]()
    httpClient.newCall(okRequest).enqueue(new Callback {
      override def onFailure(call: Call, e: IOException) = {
        promise.failure(e)
      }
      override def onResponse(call: Call, response: Response) = {
        promise.success(handleResponse(request, task.site.charset, response, task))
      }
    })
    promise.future
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
      headers = headers
    )
    page
  }


  private def getResponseContent(charset: String, response: Response) = {

    val contentBytes = response.body().bytes()
    val htmlCharset = response.body().contentType().charset()
    if (htmlCharset != null) new String(contentBytes, htmlCharset)
    else {
      log.warn(s"Charset autodetect failed, use ${Charset.defaultCharset()} as charset. Please specify charset in Site.setCharset()")
      new String(contentBytes)
    }

  }

  private def getHtmlCharset(response: Response, contentBytes: Array[Byte]): String = {
    response.body().contentType().charset().toString
  }
}

object HttpClientDownloader {
  def apply(): HttpClientDownloader = new HttpClientDownloader()
}
