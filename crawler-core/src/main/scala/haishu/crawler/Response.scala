package haishu.crawler

import java.nio.charset.Charset

import scala.collection.immutable
import haishu.crawler.util.UrlUtils.canonicalizeUrl
import haishu.crawler.selector.Selectable
import haishu.crawler.selector.Selectable

trait Response {

  def status: Int

  def headers: Map[String, Seq[String]]

  def body: Array[Byte]

  def request: Request

  def meta = request.meta

  def url = request.url

  def css(query: String): Selectable

  def css(query: String, attrName: String): Selectable

  def regex(expr: String): Selectable

  def regex(expr: String, group: Int): Selectable

  def follow(
    url: String,
    callback: Response => immutable.Seq[Either[Request, Item]] = request.callback,
    method: String = "GET",
    headers: Map[String, String] = request.headers,
    body: Array[Byte] = Array(),
    cookies: Map[String, String] = Map(),
    meta: RequestMeta = request.meta,
    encoding: Charset = request.encoding,
    errback: Exception => Unit = PartialFunction.empty) =
    Request(
      canonicalizeUrl(this.url, url),
      callback,
      method,
      headers,
      body,
      cookies,
      meta,
      encoding,
      errback
    )
}
