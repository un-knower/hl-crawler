import Dependencies._
import sbt.Keys._
import sbt._

object Commons {

  import sbtassembly.AssemblyKeys.{assembly, assemblyMergeStrategy}
  import sbtassembly.{MergeStrategy, PathList}

  def basicSettings = Seq(
    organization := "Hualongdata developer team",
    organizationName := "hualongdata.com",
    organizationHomepage := Some(url("https://hualongdata.github.io/hl-crawler")),
    homepage := Some(url("http://hualongdata.com")),
    startYear := Some(2017),
    scalaVersion := Dependencies.versionScala,
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
    //    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-encoding", "UTF-8", "-Xlint:unchecked"),
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
    assemblyMergeStrategy in assembly := {
      case PathList("javax", "servlet", xs@_*) => MergeStrategy.first
      case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
      case "application.conf" => MergeStrategy.concat
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case PathList("org", "slf4j", xs@_*) => MergeStrategy.first
      case "META-INF/native/libnetty-transport-native-epoll.so" => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    },
    fork in run := true,
    fork in Test := true,
    parallelExecution in Test := false,
    //    credentials += Credentials(Path.userHome / ".ivy2" / ".yangbajing_credentials"),
    libraryDependencies ~= {
      _ map {
        case m if m.organization == "com.typesafe.play" =>
          m.exclude("commons-logging", "commons-logging").exclude("log4j", "log4j")
        case m => m.exclude("log4j", "log4j")
      }
    },
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-library" % Dependencies.versionScala,
      "org.scala-lang" % "scala-reflect" % Dependencies.versionScala,
      _scalatest % Test
    ),
    resolvers ++= Seq(
      "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
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

  val BuildConfig = config("build") extend Compile
  val BuildSbtConfig = config("buildsbt") extend Compile

  val formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(DanglingCloseParenthesis, Preserve)
      .setPreference(IndentSpaces, 2)
      .setPreference(DoubleIndentConstructorArguments, true)
      .setPreference(NewlineAtEndOfFile, true)
      .setPreference(SpacesAroundMultiImports, false)
  }

  // invoke: build:scalariformFormat
  val buildFileSettings: Seq[Setting[_]] = SbtScalariform.scalariformSettings  ++
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

  val settings = SbtScalariform.scalariformSettings ++ Seq(
    ScalariformKeys.preferences in Compile := formattingPreferences,
    ScalariformKeys.preferences in Test := formattingPreferences
  )

}

