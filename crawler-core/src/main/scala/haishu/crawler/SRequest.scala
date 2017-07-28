package haishu.crawler

case class SRequest(
  url: String,
  callback: SResponse => Any,
  method: String,
  headers: Map[String, String],
  ) {

}
