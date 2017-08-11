package haishu.crawler

import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

import Messages.{Download, RetryRequest, ScheduleRequest}
import akka.actor.{Actor, Props}
import haishu.crawler.util.CharsetUtils
import okhttp3.{Call, Callback, Headers, MediaType, OkHttpClient, RequestBody, Request => OkRequest, Response => OkResponse}

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class OkHttpDownloader(client: OkHttpClient) extends BaseActor {

  import OkHttpDownloader._

  override def receive = {
    case Download(request) =>
      val result = downloadAsync(client, request)
      val engine = sender()
      result onComplete {
        case Success(response) =>
          log.info(s"downloading success ${request.url}")
          engine ! response
        case Failure(e) =>
          log.warning(s"download error ${request.url} $e")
          val retryTimes = request.meta.retryTimes
          if (retryTimes == 0) {
            request.errback(e)
            engine ! akka.actor.Status.Failure(e)
          } else {
            engine ! RetryRequest(request)
          }
      }
  }

}

object OkHttpDownloader {

  def props(client: OkHttpClient) = Props(new OkHttpDownloader(client))

  def downloadAsync(client: OkHttpClient, request: Request): Future[Response] = {
    val okRequest = convertRequest(request)
    val thisClient = buildClient(request.meta, client)
    val promise = Promise[Response]()
    thisClient.newCall(okRequest).enqueue(new Callback {

      override def onFailure(call: Call, e: IOException) = promise.failure(e)

      override def onResponse(call: Call, okResponse: OkResponse) = {
        val response = convertResponse(okResponse, request)
        promise.success(response)
      }
    })
    promise.future
  }

  def download(client: OkHttpClient, request: Request): Response = {
    val okRequest = convertRequest(request)
    val thisClient = buildClient(request.meta, client)
    convertResponse(thisClient.newCall(okRequest).execute(), request)
  }

  def buildClient(meta: RequestMeta, client: OkHttpClient) = {
    client.newBuilder()
      .retryOnConnectionFailure(true)
      .connectTimeout(meta.downloadTimeout, TimeUnit.MILLISECONDS)
      .followRedirects(meta.redirect)
      .proxy(meta.proxy.orNull)
      .build()
  }

  def convertRequest(request: Request): OkRequest = {
    val okRequestBody = {
      val body = request.body
      if (body.isEmpty) null
      else RequestBody.create(null, body)
    }

    val cookiesStr = request.cookies.map { case (k, v) => s"$k=$v" }.mkString("; ")
    val headersWithCookies = request.headers + ("Cookie" -> cookiesStr)

    new OkRequest.Builder()
      .url(request.url)
      .method(request.method, okRequestBody)
      .headers(Headers.of(headersWithCookies.asJava))
      .build()
  }

  def convertResponse(okResponse: OkResponse, request: Request): Response = {
    val charsetFromResponse = Option(okResponse.body().contentType()).flatMap(m => Option(m.charset()))

    var headers = Map[String, Seq[String]]()
    for (n <- okResponse.headers().names().asScala) {
      headers += (n -> okResponse.headers(n).asScala)
    }
    val body = okResponse.body()
    val bytes = body.bytes()
    val charsetFromContent = CharsetUtils.detectCharset(bytes).map(Charset.forName)
    val charset = charsetFromResponse.orElse(charsetFromContent)
    HtmlResponse(
      okResponse.code(),
      headers,
      bytes,
      request,
      charset
    )
  }

}
