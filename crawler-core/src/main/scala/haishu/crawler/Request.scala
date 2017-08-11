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
    encoding: Option[Charset] = None,
    errback: Throwable => Unit = Request.defaultErrBack,
    extra: Map[String, String] = Map()) {

  def body(str: String) = copy(body = str.getBytes(encoding.getOrElse(Charset.defaultCharset())))

  def extra(key: String, value: String): Request = extra(Seq(key -> value))

  def extra(kv: (String, String), kvs: (String, String)*): Request = extra(kv +: kvs)

  def extra(kvs: Seq[(String, String)]): Request = copy(extra = extra ++ kvs)

  def retry = copy(meta = meta.retry)

}
