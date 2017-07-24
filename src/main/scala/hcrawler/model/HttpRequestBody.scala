package hcrawler
package model

@SerialVersionUID(5659170945717023595L)
class HttpRequestBody(
    val body: Array[Byte],
    val contentType: String,
    val charset: String
    ) extends Serializable

object HttpRequestBody {

  object ContentType {

    val JSON = "application/json";

    val XML = "text/xml";

    val FORM = "application/x-www-form-urlencoded";

    val MULTIPART = "multipart/form-data";

  }

  def json(json: String, charset: String = "UTF-8"): HttpRequestBody = {
    new HttpRequestBody(json.getBytes(charset), ContentType.JSON, charset)
  }

  def xml(xml: String, charset: String = "UTF-8"): HttpRequestBody = {
    new HttpRequestBody(xml.getBytes(charset), ContentType.XML, charset)
  }

  def custom(body: Array[Byte], contentType: String, charset: String = "UTF-8"): HttpRequestBody = {
    new HttpRequestBody(body, contentType, charset)
  }

  /*def form(params: Map[String, Any], encoding: String): HttpRequestBody = {
    
  }*/
}