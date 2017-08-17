import Dependencies._
import sbt.Keys._
import sbt._

object Commons {

  def basicSettings = Seq(
    organization := "com.hualongdata",
    organizationName := "hualongdata.com",
    organizationHomepage := Some(url("https://hualongdata.github.io/hl-crawler")),
    homepage := Some(url("http://hualongdata.com")),
    startYear := Some(2017),
    scalaVersion := versionScala,
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // yes, this is 2 args
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xlint",
      // "-Yno-adapted-args", //akka-http heavily depends on adapted args and => Unit implicits break otherwise
      "-Ywarn-dead-code"
      // "-Xfuture" // breaks => Unit implicits
    ),
    scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Xlint")),
    scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-Xlint:unchecked"),
    version := "1.0.0",
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
    fork in run := true,
    fork in Test := true,
    parallelExecution in Test := false,
    //    credentials += Credentials(Path.userHome / ".ivy2" / ".yangbajing_credentials"),
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % versionScala,
      "org.scala-lang" % "scala-reflect" % versionScala,
      _scalatest % Test
    )
  ) ++ Environment.settings ++ Formatting.settings

  lazy val noPublishing = Seq(
    publish := (),
    publishLocal := (),
    publishTo := None
  )

}

object Environment {

  object BuildEnv extends Enumeration {
    val Production, Stage, Test, Developement = Value
  }

  val buildEnv = settingKey[BuildEnv.Value]("The current build environment")

  val settings = Seq(
    buildEnv := {
      sys.props.get("env")
        .orElse(sys.env.get("BUILD_ENV"))
        .flatMap {
          case "prod" => Some(BuildEnv.Production)
          case "stage" => Some(BuildEnv.Stage)
          case "test" => Some(BuildEnv.Test)
          case "dev" => Some(BuildEnv.Developement)
          case _ => None
        }
        .getOrElse(BuildEnv.Developement)
    },
    onLoadMessage := {
      // old message as well
      val defaultMessage = onLoadMessage.value
      val env = buildEnv.value
      s"""|$defaultMessage
          |Working in build environment: $env""".stripMargin
    }
  )
}

object Packaging {
  // Good example https://github.com/typesafehub/activator/blob/master/project/Packaging.scala
  import com.typesafe.sbt.SbtNativePackager._
  import com.typesafe.sbt.packager.Keys._

  // This is dirty, but play has stolen our keys, and we must mimc them here.
  val stage = TaskKey[File]("stage")
  val dist = TaskKey[File]("dist")

  import Environment.{BuildEnv, buildEnv}

  val settings = Seq(
    name in Universal := s"${name.value}",
    dist := (packageBin in Universal).value,
    mappings in Universal += {
      val confFile = buildEnv.value match {
        case BuildEnv.Developement => "dev.conf"
        case BuildEnv.Test => "test.conf"
        case BuildEnv.Stage => "stage.conf"
        case BuildEnv.Production => "prod.conf"
      }
      (sourceDirectory(_ / "universal" / "conf").value / confFile) -> "conf/application.conf"
    },
    bashScriptExtraDefines ++= Seq(
      """addJava "-Dconfig.file=${app_home}/../conf/application.conf"""",
      """addJava "-Dpidfile.path=${app_home}/../run/%s.pid"""".format(name.value),
      """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""
    ),
    bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts"),
    scriptClasspath := Seq("*"),
    mappings in(Compile, packageDoc) := Seq()
  )

}

object Formatting {

  import com.typesafe.sbt.SbtScalariform
  import com.typesafe.sbt.SbtScalariform.ScalariformKeys
  import ScalariformKeys._

  def BuildConfig = config("build") extend Compile

  def BuildSbtConfig = config("buildsbt") extend Compile

  // invoke: build:scalariformFormat
  def buildFileSettings: Seq[Setting[_]] = SbtScalariform.scalariformSettings ++
    inConfig(BuildConfig)(SbtScalariform.configScalariformSettings) ++
    inConfig(BuildSbtConfig)(SbtScalariform.configScalariformSettings) ++ Seq(
    scalaSource in BuildConfig := baseDirectory.value / "project",
    scalaSource in BuildSbtConfig := baseDirectory.value,
    includeFilter in(BuildConfig, ScalariformKeys.format) := ("*.scala": FileFilter),
    includeFilter in(BuildSbtConfig, ScalariformKeys.format) := ("*.sbt": FileFilter),
    ScalariformKeys.format in BuildConfig := {
      val x = (ScalariformKeys.format in BuildSbtConfig).value
      (ScalariformKeys.format in BuildConfig).value
    },
    ScalariformKeys.preferences in BuildConfig := formattingPreferences,
    ScalariformKeys.preferences in BuildSbtConfig := formattingPreferences
  )

  def settings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test := formattingPreferences
  )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
      .setPreference(IndentSpaces, 2)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(NewlineAtEndOfFile, true)
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(SpacesAroundMultiImports, false)
  }

}

