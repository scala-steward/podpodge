import Build.Version
import sbt.Keys._
import sbt._
import sbtwelcome._

lazy val root = project
  .in(file("."))
  .aggregate(core)
  .settings(
    addCommandAlias("run", "podpodge/run"),
    addCommandAlias("fmt", "all root/scalafmtSbt root/scalafmtAll"),
    addCommandAlias("fmtCheck", "all root/scalafmtSbtCheck root/scalafmtCheckAll"),
    logo :=
      s"""
         |    ____            __                __
         |   / __ \\____  ____/ /___  ____  ____/ /___ ____
         |  / /_/ / __ \\/ __  / __ \\/ __ \\/ __  / __ `/ _ \\
         | / ____/ /_/ / /_/ / /_/ / /_/ / /_/ / /_/ /  __/
         |/_/    \\____/\\__,_/ .___/\\____/\\__,_/\\__, /\\___/
         |                 /_/                /____/
         |
         |""".stripMargin,
    usefulTasks := Seq(
      UsefulTask("a", "run", "Runs the Podpodge server"),
      UsefulTask("b", "~podpodge/reStart", "Runs the Podpodge server with file-watch enabled"),
      UsefulTask("c", "~compile", "Compile all modules with file-watch enabled"),
      UsefulTask("d", "fmt", "Run scalafmt on the entire project")
    )
  )

lazy val core = module("podpodge", Some("core"))
  .settings(
    fork := true,
    baseDirectory in run := file("."),
    baseDirectory in reStart := file("."),
    libraryDependencies ++= Seq(
      "dev.zio"                      %% "zio"                    % Version.zio,
      "dev.zio"                      %% "zio-streams"            % Version.zio,
      "dev.zio"                      %% "zio-process"            % "0.1.0",
      "dev.zio"                      %% "zio-logging"            % "0.5.2",
      "org.scala-lang.modules"       %% "scala-xml"              % "1.3.0",
      "com.beachape"                 %% "enumeratum"             % "1.6.1",
      "io.circe"                     %% "circe-core"             % Version.circe,
      "io.circe"                     %% "circe-parser"           % Version.circe,
      "io.circe"                     %% "circe-generic"          % Version.circe,
      "org.flywaydb"                  % "flyway-core"            % "7.0.0",
      "com.typesafe.akka"            %% "akka-http"              % "10.2.1",
      "com.typesafe.akka"            %% "akka-actor-typed"       % "2.6.9",
      "com.typesafe.akka"            %% "akka-stream"            % "2.6.9",
      "com.softwaremill.sttp.client" %% "core"                   % Version.sttp,
      "com.softwaremill.sttp.client" %% "circe"                  % Version.sttp,
      "com.softwaremill.sttp.client" %% "httpclient-backend-zio" % Version.sttp,
      "org.xerial"                    % "sqlite-jdbc"            % "3.32.3.2",
      "io.getquill"                  %% "quill-jdbc"             % "3.6.0-RC2",
      "org.slf4j"                     % "slf4j-nop"              % "1.7.30"
    )
  )

def module(projectId: String, moduleFile: Option[String] = None): Project =
  Project(id = projectId, base = file(moduleFile.getOrElse(projectId)))
    .settings(Build.defaultSettings(projectId))
