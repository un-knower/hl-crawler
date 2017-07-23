package hcrawler
package model

@SerialVersionUID(5659170945717023595L)
class HttpRequestBody(
    val body: Array[Byte],
    val contentType: String,
    val encoding: String
    ) extends Serializable

object HttpRequestBody {

  object ContentType {

    val JSON = "application/json";

    val XML = "text/xml";

    val FORM = "application/x-www-form-urlencoded";

    val MULTIPART = "multipart/form-data";

  }

  def json(json: String, encoding: String): HttpRequestBody = {
    new HttpRequestBody(json.getBytes(encoding), ContentType.JSON, encoding)
  }

  def xml(xml: String, encoding: String): HttpRequestBody = {
    new HttpRequestBody(xml.getBytes(encoding), ContentType.XML, encoding)
  }

  def custom(body: Array[Byte], contentType: String, encoding: String): HttpRequestBody = {
    new HttpRequestBody(body, contentType, encoding)
  }

  /*def form(params: Map[String, Any], encoding: String): HttpRequestBody = {
    
  }*/
}