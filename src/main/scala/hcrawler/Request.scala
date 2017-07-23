package hcrawler

import utils.HttpConstant.Method.GET
import model.HttpRequestBody

@SerialVersionUID(2062192774891352043L)
case class Request(
    var url: String,
    var method: String = GET,
    var cookies: Map[String, String] = Map(),
    var headers: Map[String, String] = Map(),

    ) extends Serializable {

  private var _requestBody: HttpRequestBody = _

  def requestBody: HttpRequestBody = _requestBody

  def requestBody_=(rb: HttpRequestBody) = {
    _requestBody = rb
  }
  
  def cookie(name: String, value: String): Request = {
    cookies += name -> value
    this
  }

  def header(name: String, value: String): Request = {
    headers += name -> value
    this
  }

  override def toString(): String = {
    "Request{" +
    "url='" + url + '\'' +
    ", method='" + method + '\'' +
    ", headers=" + headers +
    ", cookies="+ cookies+
    '}'
  } 

}