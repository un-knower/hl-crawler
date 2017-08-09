package haishu.crawler

import java.nio.charset.Charset

import haishu.crawler.selector.{Html, Selectable}

case class HtmlResponse(
    status: Int,
    headers: Map[String, Seq[String]],
    body: Array[Byte],
    request: Request,
    encoding: Option[Charset] = None) extends Response {

  private lazy val text = new String(body, encoding.getOrElse(Charset.defaultCharset()))

  private lazy val html = Html(text, request.url)

  def css(query: String): Selectable = html.css(query)

  def css(query: String, attrName: String): Selectable = html.css(query, attrName)

  def regex(expr: String): Selectable = html.regex(expr)

  def regex(expr: String, group: Int): Selectable = html.regex(expr, group)

}
