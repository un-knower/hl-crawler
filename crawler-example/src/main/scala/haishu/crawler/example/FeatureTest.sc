import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ThrottleMode}
import akka.stream.scaladsl.Source

import scala.concurrent.duration._

implicit val system = ActorSystem("system")
implicit val materializer = ActorMaterializer()
implicit val ec = system.dispatcher

val s = Source(1 to 100)
val fact = s.scan(BigInt(1))(_ * _)
fact
  .zipWith(Source(1 to 100))((n, idx) => s"$idx = $n")
  .throttle(1, 1.second, 1, ThrottleMode.shaping)
