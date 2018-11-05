import sbt.Keys.scalaVersion

name := "hocones"

version := "0.1"

scalaVersion := "2.12.6"

val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import",
  "-Ypartial-unification"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

val defaultSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.6",
  organization := "pl.onewebpro",
  scalacOptions := compilerOptions
)

val circeVersion = "0.9.0"

val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-yaml"
).map(_ % circeVersion)

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

val markdown = Seq(
  "net.steppschuh.markdowngenerator" % "markdowngenerator" % "1.3.1.1"
)

val scalaTestVersion = "3.0.5"
val scalaMockVersion = "4.1.0"

val tests = Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalamock" %% "scalamock" % scalaMockVersion % "test"
)

val `hocones-parser` =
  (project in file("hocones-parser"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-parser",
      libraryDependencies ++= (hocon ++ logs ++ fp ++ tests)
    )

val `hocones-environment-files` =
  (project in file("hocones-environment-files"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-environment-files",
      libraryDependencies ++= (logs ++ fp ++ tests)
    ).dependsOn(`hocones-parser`)

val `hocones-meta` =
  (project in file("hocones-meta"))
    .settings(defaultSettings)
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version),
      buildInfoPackage := "pl.onewebpro.hocones.meta"
    )
    .settings(
      name := "hocones-meta",
      libraryDependencies ++= (logs ++ fp ++ circe ++ tests),
      addCompilerPlugin(
        "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
      )
    ).dependsOn(`hocones-parser`)

val `hocones-md-docs` =
  (project in file("hocones-md-docs"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-md-docs",
      libraryDependencies ++= (logs ++ fp ++ markdown ++ tests)
    ).dependsOn(`hocones-parser`, `hocones-meta`)

val `hocones-statistics` =
  (project in file("hocones-statistics"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-statistics",
      libraryDependencies ++= (logs ++ fp ++ tests)
    ).dependsOn(`hocones-parser`)

val `hocones-cli` =
  (project in file("hocones-cli"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-cli",
      libraryDependencies ++= (cli ++ hocon ++ logs ++ fp ++ tests)
    )
    .dependsOn(`hocones-environment-files`, `hocones-statistics`, `hocones-meta`, `hocones-md-docs`)

lazy val root = (project in file("."))
  .aggregate(
    `hocones-parser`,
    `hocones-environment-files`,
    `hocones-cli`,
    `hocones-statistics`,
    `hocones-meta`,
    `hocones-md-docs`
  )