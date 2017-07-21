import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.util.ByteString
import hcrawler.Site
import hcrawler.util.HttpConstant
import org.jsoup.Jsoup

implicit val system = ActorSystem("asd")
implicit val dispatcher = system.dispatcher
implicit val materializer = ActorMaterializer()


val url = "http://www.stats.gov.cn/tjsj/zxfb/"


val resp = Http().singleRequest(HttpRequest(uri = url))

resp.foreach { resp =>
  resp.entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach {
    body =>
      println(body.utf8String)
  }
}