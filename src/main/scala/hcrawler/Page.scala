package hcrawler

import org.apache.commons.lang3.StringUtils
import selector.{Html, Selectable}
import utils.UrlUtils.canonicalizeUrl
import utils.HttpConstant.StatusCode

case class Page (
    val request: Request,
    var rawText: String,
    val url: Selectable,
    val headers: Map[String, Seq[String]],
    val statusCode: Int = StatusCode.OK,
    val downloadSuccess: Boolean = true,
    var targetRequests: Seq[Request] = Vector()) {

  private val _resultItems = ResultItems(request)

  // parse html if needed for performance
  lazy val html: Html = Html(rawText, request.url)

  def resultItems: ResultItems = _resultItems

  def skip(s: Boolean): Page = {
    resultItems.skip = true
    this
  }

  def putField[T](field: String, value: T): Unit = {
    resultItems.put(field, value)
  }

  def addTargetRequests(requests: Seq[String]): Unit = {
    for (s <- requests) {
      if (!(StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:"))) {
        val req = new Request(canonicalizeUrl(s, url.toString()))
        targetRequests :+= req
      }
    }
  }

  /*def addTargetRequest(requests: Seq[String], priority: Long): Unit = {
    for (s <- requests) {
      if (!(StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:"))) {
        val req = new Request(UrlUtils.canonicalizeUrl(s, url.toString())).priority(priority)
        targetRequests += req
      }
    }
  }*/

  def addTargetRequest(requestString: String): Unit = {
    if (!(StringUtils.isBlank(requestString) ||
          requestString.equals("#") ||
          requestString.startsWith("javascript:"))) {
      val req = Request(canonicalizeUrl(requestString, url.toString()))
      targetRequests :+= req
    }
  }

  def addTargetRequest(request: Request): Unit = {
    targetRequests :+= request
  }

  def rawText(rt: String): Page = {
    rawText = rt
    this
  }

  override def toString: String = {
    "Page{" +
    "request=" + request +
    ", resultItems=" + resultItems +
    ", html=" + html +
    ", rawText='" + rawText + '\'' +
    ", url=" + url +
    ", headers=" + headers +
    ", statusCode=" + statusCode +
    ", success=" + downloadSuccess +
    ", targetRequests=" + targetRequests +
    '}'
  }


}