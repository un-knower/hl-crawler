package haishu.crawler

import java.nio.charset.Charset
import scala.collection.immutable

object Request {

  def defaultErrBack(e: Throwable) = e match {
    case _: java.io.IOException =>
  }

}

case class Request(
    url: String,
    callback: Response => immutable.Seq[Either[Request, Item]],
    method: String = "GET",
    headers: Map[String, String] = Map(),
    body: Array[Byte] = Array(),
    cookies: Map[String, String] = Map(),
    meta: RequestMeta = RequestMeta(),
    encoding: Charset = Charset.defaultCharset(),
    errback: Throwable => Unit = Request.defaultErrBack) {

  def body(str: String) = copy(body = str.getBytes(encoding))

  def retry = copy(meta = meta.retry)

}
