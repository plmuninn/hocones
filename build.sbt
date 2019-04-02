import microsites._
import sbt.Keys.scalaVersion

name := "hocones"

version := "0.1"

scalaVersion := "2.12.6"

val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import",
  "-Ypartial-unification",
  "-Xfatal-warnings"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

val defaultSettings = Seq(
  version := "0.1",
  scalaVersion := "2.12.6",
  organization := "pl.muninn",
  scalacOptions := compilerOptions
)

val circeVersion = "0.9.0"

val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-yaml"
).map(_ % circeVersion)

val declineVersion = "0.5.0"
val fansiVersion = "0.2.5"
val console4CatsVersion = "0.3"

val cli = Seq(
  "com.monovore" %% "decline" % declineVersion,
  "com.lihaoyi" %% "fansi" % fansiVersion,
  "com.github.gvolpe" %% "console4cats" % console4CatsVersion
)

val lightbendConfigVersion = "1.3.2"

val hocon = Seq(
  "com.typesafe" % "config" % lightbendConfigVersion
)

val logbackVersion = "1.2.3"
val scalaLoggingVersion = "3.9.0"
val log4CatsVersion = "0.2.0"
val pprintVersion = "0.5.3"

val logs = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "com.lihaoyi" %% "pprint" % pprintVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "io.chrisdavenport" %% "log4cats-core" % log4CatsVersion,
  "io.chrisdavenport" %% "log4cats-extras" % log4CatsVersion,
  "io.chrisdavenport" %% "log4cats-slf4j" % log4CatsVersion
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
  "pl.muninn" %% "scala-md-tag" % "0.2.2"
)

val scalaTestVersion = "3.0.5"
val scalaMockVersion = "4.1.0"

val tests = Seq(
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalamock" %% "scalamock" % scalaMockVersion % "test"
)

val `hocones-common` =
  (project in file("hocones-common"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-common",
      libraryDependencies ++= (hocon ++ logs ++ fp ++ tests)
    )

val `hocones-parser` =
  (project in file("hocones-parser"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-parser",
      libraryDependencies ++= (hocon ++ logs ++ fp ++ tests)
    )
    .dependsOn(`hocones-common`)

val `hocones-meta-file` =
  (project in file("hocones-meta-file"))
    .settings(defaultSettings)
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](name, version),
      buildInfoPackage := "pl.muninn.hocones.meta"
    )
    .settings(
      name := "hocones-meta-file",
      libraryDependencies ++= (logs ++ fp ++ circe ++ tests),
      addCompilerPlugin(
        ("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)
      )
    )
    .dependsOn(`hocones-parser`, `hocones-common`)

val `hocones-meta-document` =
  (project in file("hocones-meta-document"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-meta-document",
      libraryDependencies ++= (logs ++ fp ++ tests),
      addCompilerPlugin(
        ("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)
      )
    )
    .dependsOn(`hocones-meta-file`)

val `hocones-environment-files` =
  (project in file("hocones-environment-files"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-environment-files",
      libraryDependencies ++= (logs ++ fp ++ tests)
    )
    .dependsOn(`hocones-parser`, `hocones-common`, `hocones-meta-document`)

val `hocones-md-docs` =
  (project in file("hocones-md-docs"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-md-docs",
      libraryDependencies ++= (logs ++ fp ++ markdown ++ tests)
    )
    .dependsOn(`hocones-parser`, `hocones-meta-document`, `hocones-common`)

val `hocones-statistics` =
  (project in file("hocones-statistics"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-statistics",
      libraryDependencies ++= (logs ++ fp ++ tests)
    )
    .dependsOn(`hocones-parser`)

val `hocones-cli` =
  (project in file("hocones-cli"))
    .settings(defaultSettings)
    .settings(
      name := "hocones-cli",
      mainClass in assembly := Some("pl.muninn.hocones.cli.Main"),
      libraryDependencies ++= (cli ++ hocon ++ logs ++ fp ++ tests),
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", _ @_*) => MergeStrategy.discard
        case other: Any                  => MergeStrategy.defaultMergeStrategy(other)
      }
    )
    .enablePlugins(GraalVMNativeImagePlugin)
    .dependsOn(
      `hocones-environment-files`,
      `hocones-statistics`,
      `hocones-meta-file`,
      `hocones-meta-document`,
      `hocones-md-docs`
    )

lazy val `sbt-hocones` =
  (project in file("sbt-hocones"))
    .settings(defaultSettings)
    .settings(
      name := "sbt-hocones",
      libraryDependencies ++= (hocon ++ logs ++ fp),
      sbtPlugin := true,
      sbtVersion := "1.2.3",
      libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value,
      scriptedBufferLog := false
    )
    .dependsOn(
      `hocones-common`,
      `hocones-parser`,
      `hocones-environment-files`,
      `hocones-meta-file`,
      `hocones-meta-document`,
      `hocones-md-docs`
    )

lazy val root = (project in file("."))
  .enablePlugins(MicrositesPlugin)
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    micrositeName := "Hocones",
    micrositeDescription := "Hocon configuration toolset and documentation library",
    micrositeUrl := "https://plmuninn.github.io",
    micrositeBaseUrl := "/hocones",
    micrositeHomepage := "https://plmuninn.github.io/hocones/",
    micrositeAuthor := "Maciej Romański Muninn Software",
    micrositeGithubOwner := "plmuninn",
    micrositeGithubRepo := "hocones",
    micrositeHighlightTheme := "atom-one-light",
    micrositePushSiteWith := GHPagesPlugin,
    micrositeCompilingDocsTool := WithMdoc,
    micrositeExtraMdFiles := Map(
      file("README.md") -> ExtraMdFileConfig(
        "index.md",
        "home",
        Map("title" -> "Home", "section" -> "home", "position" -> "0")
      )
    )
  )
  .aggregate(
    `hocones-common`,
    `hocones-parser`,
    `hocones-meta-file`,
    `hocones-meta-document`,
    `hocones-statistics`,
    `hocones-environment-files`,
    `hocones-md-docs`,
    `hocones-cli`
  )
