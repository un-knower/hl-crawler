package haishu.crawler

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object Config {

  val config = ConfigFactory.load()

  private implicit class AsScalaDuration(jduration: java.time.Duration) {
    def asScala: FiniteDuration = Duration.fromNanos(jduration.toNanos)
  }

  def durationConfig(path: String, default: FiniteDuration): FiniteDuration =
    try {
      config.getDuration(path).asScala
    } catch {
      case _: com.typesafe.config.ConfigException.Missing => default
    }

  def intConfig(path: String, default: Int): Int =
    try {
      config.getInt(path)
    } catch {
      case _: com.typesafe.config.ConfigException.Missing => default
    }

  def longConfig(path: String, default: Long): Long =
    try {
      config.getLong(path)
    } catch {
      case _: com.typesafe.config.ConfigException.Missing => default
    }

  val noRequestTimes = intConfig("hlcrawler.no-request-times", 25)

  val intervalBetweenRequest = durationConfig("hlcrawler.interval-between-request", 200.millis)

  val downloadTimtout = durationConfig("hlcrawler.download-timeout", 10.seconds)

}
