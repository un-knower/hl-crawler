import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Source
import io.circe.syntax._

val m = Map("title" -> "23", "onet" -> 1)

val s = m.asJson




