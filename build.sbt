
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

Compile / run / fork := true

ThisBuild / Compile / run / fork := true

val weaverVersion = "0.8.1"
val http4sVersion = "0.23.11"
val fs2Version =  "3.11.0"
val log4catsVersion =  "2.3.1"


lazy val root = (project in file("."))
  .settings(
    name := "txodds-tech-test",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "com.disneystreaming" %% "weaver-scalacheck" % weaverVersion % Test,
      "com.disneystreaming" %% "weaver-cats" % weaverVersion % Test,
      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "co.fs2" %% "fs2-core" % fs2Version,
      "co.fs2" %% "fs2-io" % fs2Version,
      "org.typelevel"    %% "log4cats-slf4j"      % "2.6.0",
      "ch.qos.logback"   %  "logback-classic"     % "1.2.11",
      "org.http4s"       %% "http4s-dsl"          % http4sVersion,
      "org.typelevel" %% "munit-cats-effect" % "2.0.0" % "test",
      "org.scalamock" %% "scalamock" % "5.2.0",
      "org.scalatest" %% "scalatest" % "3.2.16" % Test
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    scalacOptions += "-Wnonunit-statement"
  )
