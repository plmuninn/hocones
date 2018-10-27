import sbt.Keys.scalaVersion

name := "hocon-utils"

version := "0.1"

scalaVersion := "2.12.6"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

val defaultSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.6",
  organization := "pl.onewebpro",
  scalacOptions += "-Ypartial-unification"
)

val scalaOptVersion = "3.7.0"
val console4CatsVersion = "0.3"

val cli = Seq(
  "com.github.scopt" %% "scopt" % scalaOptVersion,
  "com.github.gvolpe" %% "console4cats" % console4CatsVersion
)

val lightbendConfigVersion = "1.3.2"

val hocon = Seq(
  "com.typesafe" % "config" % lightbendConfigVersion
)

val logbackVersion = "1.2.3"
val scalaLoggingVersion = "3.9.0"

val logs = Seq(
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
)

val catsVersion = "1.3.1"
val catsEffectVersion = "1.0.0"

val shaplessVersion = "2.3.3"

val fp = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "com.chuusai" %% "shapeless" % shaplessVersion
)

val monocleVersion = "1.5.0"

val monocle = Seq(
  "com.github.julien-truffaut" %% "monocle-core" % monocleVersion,
  "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion
)

val scalaTestVersion = "3.0.5"
val scalaMockVersion = "4.1.0"

val tests = Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalamock" %% "scalamock" % scalaMockVersion % "test"
)

val hoconParser =
  (project in file("hocon-parser"))
    .settings(defaultSettings)
    .settings(
      name := "hocon-parser",
      libraryDependencies ++= (hocon ++ logs ++ fp ++ monocle ++ tests),
      libraryDependencies += "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
    )

val environmentFiles =
  (project in file("environment-files"))
    .settings(defaultSettings)
    .settings(
      name := "environment-files",
      libraryDependencies ++= (logs ++ fp ++ tests)
    ).dependsOn(hoconParser)

val hoconesStatistics =
  (project in file("hocones-statistics"))
  .settings(defaultSettings)
  .settings(
    name := "hocones-statistics",
    libraryDependencies ++= (logs ++ fp ++ tests)
  ).dependsOn(hoconParser)

val cliFrontend =
  (project in file("cli-frontend"))
    .settings(defaultSettings)
    .settings(
      name := "cli-frontend",
      libraryDependencies ++= (cli ++ hocon ++ logs ++ fp ++ tests)
    )
    .dependsOn(environmentFiles, hoconesStatistics)

lazy val root = (project in file("."))
  .aggregate(hoconParser, environmentFiles, cliFrontend, hoconesStatistics)