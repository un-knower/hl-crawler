import sbt._

object Dependencies {
  val _jsoup = "org.jsoup" % "jsoup" % "1.10.2"

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

  val _logbackClassic = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val _scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
}