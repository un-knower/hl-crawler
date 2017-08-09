package haishu.crawler

import java.nio.charset.Charset
import scala.collection.immutable

case class Request(
    url: String,
    callback: Response => immutable.Seq[Either[Request, Item]],
    method: String = "GET",
    headers: Map[String, String] = Map(),
    body: Array[Byte] = Array(),
    cookies: Map[String, String] = Map(),
    meta: RequestMeta = RequestMeta(),
    encoding: Charset = Charset.defaultCharset(),
    errback: Exception => Unit = _ => ()) {

  def body(str: String) = copy(body = str.getBytes(encoding))

}
