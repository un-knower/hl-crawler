import Dependencies._

name := "hcrawler"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  _jsoup,
  _akkaHttp,
  _akkaActor,
  _akkaStream,
  _scalaLogging,
  _logbackClassic
)

libraryDependencies += "org.asynchttpclient" % "async-http-client" % "2.0.33"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.3.0"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.6"