package haishu.crawler

import com.typesafe.config.ConfigFactory

import scala.util.Try

object Config {

  val config = ConfigFactory.load()

  val defaultNoRequestTimes = 25
  val noRequestTimes = Try(config.getInt("hlcrawler.no-request-times")).toOption.getOrElse(defaultNoRequestTimes)

}
