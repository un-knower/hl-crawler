import Commons._
import Dependencies._
import sbt._

lazy val root = Project(id = "hl-crawler-root", base = file("."))
  .aggregate(crawlerExample, crawlerCore, crawlerCommon)
  .settings(Formatting.buildFileSettings: _*)
  .settings(noPublishing: _*)

lazy val crawlerExample = project("crawler-example")
  .dependsOn(crawlerCore)
  .settings(
    libraryDependencies ++= Seq(
      _akkaHttp
    ),
    connectInput in run := true
  )

lazy val crawlerCore = project("crawler-core")
  .dependsOn(crawlerCommon)
  .settings(
    libraryDependencies ++= Seq(
      _akkaStream) ++
      _circe ++
      _okhttp
  )

lazy val crawlerCommon = project("crawler-common")
  .settings(
    libraryDependencies ++= Seq(
      _akkaActor,
      _jsoup,
      _commonsLang3,
      _scalaLogging,
      _logbackClassic
    ) ++
    _jackson
  )

def project(name: String, subPath: String = "") =
  Project(id = name, base = file(if (subPath == "") name else subPath + "/" + name))
    .settings(basicSettings: _*)
