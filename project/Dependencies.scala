import sbt._

object Dependencies {
  val versionScala = "2.12.2"

  val _scalatest = "org.scalatest" %% "scalatest" % "3.0.3"

  private val versionAkkaHttp = "10.0.9"
  val _akkaHttp = ("com.typesafe.akka" %% "akka-http" % versionAkkaHttp)
    .exclude("com.typesafe.akka", "akka-actor_2.12")
    .exclude("com.typesafe.akka", "akka-stream_2.12")
  val _akkaHttpTest = ("com.typesafe.akka" %% "akka-http-testkit" % versionAkkaHttp)
    .exclude("com.typesafe.akka", "akka-stream_2.12")
    .exclude("com.typesafe.akka", "akka-http_2.12")
    .exclude("com.typesafe.akka", "scala-library")

  private val versionAkka = "2.5.3"
  val _akkaActor = "com.typesafe.akka" %% "akka-actor" % versionAkka
  val _akkaSlfj = "com.typesafe.akka" %% "akka-slf4j" % versionAkka
  val _akkaStream = "com.typesafe.akka" %% "akka-stream" % versionAkka

  val _scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

  val _jsoup = "org.jsoup" % "jsoup" % "1.10.2"

  val _logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"

  val _seleniumJava = "org.seleniumhq.selenium" % "selenium-java" % "3.4.0"

  val _commonsLang3 = "org.apache.commons" % "commons-lang3" % "3.6"

  private val versionOkhttp = "3.8.1"
  val _okhttp = Seq(
    "com.squareup.okhttp3" % "okhttp" % versionOkhttp,
    "com.squareup.okhttp3" % "okhttp-urlconnection" % versionOkhttp
  )

}

