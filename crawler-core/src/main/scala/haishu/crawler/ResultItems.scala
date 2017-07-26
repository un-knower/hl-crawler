package haishu.crawler

case class ResultItems private[crawler] (
    var request: Request,
    var skip: Boolean = false) {

  private var fields = Map[String, Any]()

  def put[T](field: String, value: T): ResultItems = {
    if (value != null) fields += field -> value
    this
  }

  def get[T](field: String): Option[T] = {
    fields.get(field).map(_.asInstanceOf[T])
  }

  def getAll: Map[String, Any] = fields

  override def toString: String =
    s"ResultItems{fields=$fields, request=$request, skip=$skip}"

}

/*case class ResultItems private (
    request: Request,
    skip: Boolean = false,
    fields: Map[String, Any] = Map[String, Any]()
    ) {

  def put[T](field: String, value: T) = copy(fields = fields + (field -> value))

  def get[T](field: String): Option[T] = {
    fields.get(field).map(_.asInstanceOf[T])
  }
}*/