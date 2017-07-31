package haishu.crawler

import javax.management.Query

import haishu.crawler.selector.{Html, Selectable}
import haishu.crawler.util.HttpConstant.StatusCode
import haishu.crawler.util.UrlUtils.canonicalizeUrl
import org.apache.commons.lang3.StringUtils

case class Page(
    request: Request,
    rawText: String,
    url: Selectable,
    headers: Map[String, Seq[String]],
    statusCode: Int = StatusCode.OK,
    downloadSuccess: Boolean = true,
    var targetRequests: Seq[Request] = Vector()) {

  private val _resultItems = ResultItems(request)

  // lazy for Page.fail
  lazy val html: Html = Html(rawText, request.url)

  def resultItems: ResultItems = _resultItems

  def skip(): Page = {
    resultItems.isSkip = true
    this
  }

  def css(query: String): Selectable = html.css(query)

  def css(query: String, attr: String): Selectable = html.css(query, attr)

  def regex(expr: String): Selectable = html.regex(expr)

  def regex(expr: String, group: Int) = html.regex(expr, group)

  def put[T](field: String, value: T): Unit = value match {
    case s: Selectable =>
      s.get().foreach { v =>
        resultItems.put(field, v)
      }
    case _ => resultItems.put[T](field, value)
  }

  def addTargetRequests(urls: Seq[String]): Unit = {
    for (s <- urls) {
      if (!(StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:"))) {
        val req = Request(canonicalizeUrl(url.get().orNull, s))
        targetRequests :+= req
      }
    }
  }

  def follow(url: String, rest: String*): Unit = follow(url +: rest)

  def follow(urls: Seq[String]): Unit = addTargetRequests(urls)

  def follow(s: Selectable): Unit = follow(s.all())

  def addTargetRequest(requestString: String): Unit = addTargetRequests(Seq(requestString))

  def addTargetRequest(request: Request): Unit = {
    targetRequests :+= request
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

object Page {
  private[crawler] def fail(): Page = Page(request = null, rawText = null, url = null, headers = null, downloadSuccess = false)
}
