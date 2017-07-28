package haishu.crawler

import haishu.crawler.model.HttpRequestBody
import haishu.crawler.util.HttpConstant.Method.GET

@SerialVersionUID(2062192774891352043L)
case class Request(
    url: String,
    method: String = GET,
    cookies: Map[String, String] = Map(),
    headers: Map[String, String] = Map(),
    requestBody: Option[HttpRequestBody] = None,
    extras: Map[String, Any] = Map()) extends Serializable {

  def requestBody(rb: HttpRequestBody): Request = copy(requestBody = Some(rb))

  def cookies(c: Seq[(String, String)]): Request = copy(cookies = cookies ++ c)

  def cookies(c: Map[String, String]): Request = cookies(c.toSeq)

  def cookies(c: (String, String), rest: (String, String)*): Request = cookies(c +: rest)

  def cookie(name: String, value: String): Request = cookies(name -> value)

  def headers(h: Seq[(String, String)]): Request = copy(headers = headers ++ h)

  def headers(h: Map[String, String]): Request = headers(h.toSeq)

  def headers(h: (String, String), rest: (String, String)*): Request = headers(h +: rest)

  def header(name: String, value: String): Request = headers(name -> value)

  def method(methodName: String): Request = copy(method = methodName)

  def extra[T](key: String, value: T): Request = copy(extras = extras + (key -> value))

  def getExtra[T](key: String): Option[T] = extras.get(key).map(_.asInstanceOf[T])

  override def toString: String = {
    "Request{" +
      "url='" + url + '\'' +
      ", method='" + method + '\'' +
      ", headers=" + headers +
      ", cookies=" + cookies +
      '}'
  }

}

object Request {
  val cycleTriedTimes = "_cycle_tried_times"
}
