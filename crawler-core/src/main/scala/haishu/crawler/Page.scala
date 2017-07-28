package haishu.crawler

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

  def skip(s: Boolean): Page = {
    resultItems.skip = true
    this
  }

  def putField[T](field: String, value: T): Unit = {
    resultItems.put(field, value)
  }

  def addTargetRequests(urls: Seq[String]): Unit = {
    for (s <- urls) {
      if (!(StringUtils.isBlank(s) || s.equals("#") || s.startsWith("javascript:"))) {
        val req = Request(canonicalizeUrl(url.toString, s))
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
