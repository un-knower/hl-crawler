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