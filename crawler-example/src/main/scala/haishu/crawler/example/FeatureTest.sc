import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Source
import io.circe.syntax._

val m = Map("title" -> "23", "onet" -> 1)

val ru = scala.reflect.runtime.universe

case class Foo(x: Int)

case class Bar(x: Int)

val t = ru.typeOf[Foo]

val t2 = ru.typeOf[Bar]




