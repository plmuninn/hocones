# Sbt plugin

Hocones sbt plugin will run and create documentation for your project in compilation time.
It will use project class loader - thanks of that all project configurations will be loaded.

## Installation

Add in project, you need to add in `project/plugins.sbt`:

```sbtshell
addSbtPlugin("pl.muninn" % "sbt-hocones" % "0.1")
```

And in `build.sbt` enable it

```sbtshell
lazy val root = (project in file("."))
  .enablePlugins(HoconesPlugin)
```

And this is it. Plugin will run during compilation.

If you want to run it without compilation just run command `sbt hocones`

### Sbt plugin configurations

* `hocones` - name of command to run
* `configFile` - you can set path in config that should be used during generation of documentation - other paths will be ignored
* `ignoredPaths` - list of paths to ignore (like akka, monix etc.) - few popular paths are ignored by default
* `configFileToLoad` - which configuration file should be loaded - default is application.conf
* `pathForSave` - where documentation should be saved - default is `{module}/src/main/resources/hocones`
* `createEnvironmentFile` - should env file be created - default true
* `environmentFileWithComments` - should environment file contain comments - default true
* `environmentFileWithDefaults` - should environment file contain defaults - default true
* `environmentWithoutDuplicates` - should environment file be created without duplicates - default true
* `createDocumentationFile` - should file with full documentation be created - default true
* `createEnvironmentDocumentationTableFile` - should file with environment documentation be created - default true
* `environmentDocumentationFileTableAlignment` - what alignment should be used in environment document - default left - possible values (left, right, center)

Example of configuration:

```sbtshell
lazy val root = (project in file("."))
  .enablePlugins(HoconesPlugin)
  .settings(
    loadConfigPath := Some("pl.muninn"),
    ignoredPaths ++= Seq("play.http", "play.auth"),
    pathForSave := file(".") / "documentation",
    createEnvironmentFile := false
  )

```

