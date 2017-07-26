import Commons._
import Dependencies._
import sbt._

lazy val root = Project(id = "intelligence-system-root", base = file("."))
  .dependsOn(hlCommon)
  .settings(Formatting.buildFileSettings: _*)
  .settings(noPublishing: _*)
  .settings(
    libraryDependencies ++= Seq(
    ) ++ _okhttp
  )

lazy val hlCommon = project("hl-common")
  .settings(
    libraryDependencies ++= Seq(
      _jsoup,
      _akkaActor,
      _akkaStream,
      _commonsLang3,
      _scalaLogging,
      _logbackClassic
    )
  )

def project(name: String, subPath: String = "") =
  Project(id = name, base = file(if (subPath == "") name else subPath + "/" + name))
    .settings(basicSettings: _*)

