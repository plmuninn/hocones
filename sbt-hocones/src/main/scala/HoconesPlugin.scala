import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}
import sbt.plugins.JvmPlugin
import sbt._
import Keys._
import sbt.internal.inc.classpath.ClasspathUtilities
import cats.effect.IO
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import java.nio.file.Paths

import pl.onewebpro.hocones.env.EnvironmentFileGenerator
import pl.onewebpro.hocones.md.MdGenerator
import pl.onewebpro.hocones.md.config.Configuration.{DocumentConfiguration, TableAlignment, TableConfiguration}
import pl.onewebpro.hocones.meta.{model, MetaFile}
import pl.onewebpro.hocones.meta.config.Configuration.MetaConfiguration
import pl.onewebpro.hocones.meta.document.GenerateDocumentation
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.{HoconParser, HoconResult}

object HoconesPlugin extends AutoPlugin {

  // List of ignored paths - system paths
  lazy val ignoredSystemPaths = Seq(
    "gopherProxySet",
    "ftp",
    "os",
    "line",
    "sun",
    "path",
    "ssl-config",
    "file",
    "java",
    "jnidispatch",
    "jline",
    "http",
    "socksNonProxyHosts",
    "jna",
    "user",
    "awt",
  )

  lazy val popularIgnoredPaths = Seq(
    "akka",
    "spark",
    "slick",
    "monix"
  )

  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = JvmPlugin

  object autoImport extends HoconesKeys

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    unmanagedClasspath in Compile ++= (unmanagedResources in Compile).value,
    loadConfigPath := None,
    ignoredPaths := popularIgnoredPaths,
    configFileToLoad := (resourceDirectory in Compile).value / "application",
    pathForSave := (resourceDirectory in Compile).value / "hocones",
    createEnvironmentFile := true,
    environmentFileWithComments := true,
    environmentFileWithDefaults := true,
    environmentWithoutDuplicates := true,
    createDocumentationFile := true,
    createEnvironmentDocumentationTableFile := true,
    environmentDocumentationFileTableAlignment := "left",
    hocones := hoconesTask.value,
    compile in Compile := (compile in Compile).dependsOn(hoconesTask).value
  )

  private lazy val classLoaderTask = Def.task {
    ClasspathUtilities.toLoader(Attributed.data((Compile / dependencyClasspath).value))
  }

  private lazy val loadedConfigTask = Def.taskDyn {
    val log = streams.value.log
    val classLoader = classLoaderTask.value
    val fileToLoad = configFileToLoad.value
    val defaultResourcesToLoad = (resourceDirectory in Compile).value

    Def.task {
      log.info(s"Loading configuration file ${fileToLoad.getAbsolutePath}")

      if (fileToLoad.getParentFile.getPath == defaultResourcesToLoad.getPath) {
        ConfigFactory.parseResourcesAnySyntax(classLoader, fileToLoad.getName)
      } else {
        ConfigFactory.parseFileAnySyntax(fileToLoad)
      }
    }
  }

  private lazy val configTask = Def.taskDyn {
    val pathToLoad = loadConfigPath.value

    loadedConfigTask
      .map { config =>
        (ignoredSystemPaths ++ ignoredPaths.value).foldLeft(config.root) {
          case (cfg, path) => cfg.withoutKey(path)
        }
      }
      .map { config =>
        pathToLoad.map(path => config.withOnlyKey(path)).getOrElse(config)
      }
  }

  lazy val inputPathTask = Def.taskDyn {
    val log = streams.value.log
    val fileToLoad = configFileToLoad.value
    val pathName = fileToLoad.getName
    val path = pathForSave.value

    Def.task {
      log.info(s"Configuration will be saved in ${path.getAbsolutePath}")

      if (!path.exists()) {
        log.info(s"Creating ${path.getAbsolutePath} dir")
        path.mkdirs()
      }

      Paths.get(path.getPath + s"/$pathName")
    }
  }

  lazy val tableAlignmentTask = Def.taskDyn {
    val alignment = environmentDocumentationFileTableAlignment.value

    Def.task {
      alignment match {
        case "left"   => TableAlignment.Left
        case "right"  => TableAlignment.Right
        case "center" => TableAlignment.Center
        case value =>
          throw new IllegalArgumentException(
            s"$value is not a proper alignment - possible values are (left, right, center)"
          )
      }
    }
  }

  private lazy val hoconesTask = Def.taskDyn[Unit] {
    val log = streams.value.log
    val inputPath = inputPathTask.value
    val loadedConfig = configTask.value

    val createEnvironmentFileSetting = createEnvironmentFile.value
    val environmentFileWithCommentsSetting = environmentFileWithComments.value
    val environmentFileWithDefaultsSetting = environmentFileWithDefaults.value
    val environmentWithoutDuplicatesSetting = environmentWithoutDuplicates.value

    val createEnvironmentDocsFileSetting = createEnvironmentDocumentationTableFile.value
    val tableAlignmentTaskSetting = tableAlignmentTask.value

    val createDocumentationFileSetting = createDocumentationFile.value

    def getOutputPath(ext: String) =
      Paths.get(inputPath.toFile.getPath + ext)

    Def.task {
      val loadWithMeta: IO[(HoconResult, model.MetaInformation)] =
        for {
          _ <- IO.unit
          _ = log.info("Parsing configuration")
          _ = log.debug(loadedConfig.render(ConfigRenderOptions.concise().setFormatted(true)))
          result <- HoconParser(loadedConfig.toConfig)
          _ = log.info("Loading metadata for configuration")
          metaFileWithMeta <- MetaFile.load(MetaConfiguration(input = inputPath.toFile), result).toIO
          (_, meta) = metaFileWithMeta
        } yield (result, meta)

      val environmentFile: (HoconResult, MetaInformation) => IO[Unit] = (hocon: HoconResult, meta: MetaInformation) =>
        if (createEnvironmentFileSetting) for {
          _ <- IO.unit
          config = EnvironmentConfiguration(
            getOutputPath(".env"),
            environmentFileWithCommentsSetting,
            environmentFileWithDefaultsSetting,
            environmentWithoutDuplicatesSetting
          )
          _ = log.info("Generating environment file for configuration")
          _ <- EnvironmentFileGenerator.run(config, hocon, meta).toIO
        } yield ()
        else IO.unit

      val environmentDocs: (HoconResult, MetaInformation) => IO[Unit] = (hocon: HoconResult, meta: MetaInformation) =>
        if (createEnvironmentDocsFileSetting) for {
          _ <- IO.unit
          tableConfig = TableConfiguration(getOutputPath(".env.md"), tableAlignmentTaskSetting)
          documentation <- GenerateDocumentation.generate(hocon, meta).toIO
          _ = log.info("Generating environment table for configuration")
          _ <- MdGenerator
            .generateTable(hocon, meta, documentation, tableConfig)
            .toIO
        } yield ()
        else IO.unit

      val docs: (HoconResult, MetaInformation) => IO[Unit] = (hocon: HoconResult, meta: MetaInformation) =>
        if (createDocumentationFileSetting) for {
          _ <- IO.unit
          documentConfiguration = DocumentConfiguration(getOutputPath(".md"))
          documentation <- GenerateDocumentation.generate(hocon, meta).toIO
          _ = log.info("Generating documentation for configuration")
          _ <- MdGenerator
            .generateDocument(hocon, documentation, documentConfiguration)
            .toIO
        } yield ()
        else IO.unit

      (for {
        hoconWithMeta <- loadWithMeta
        (hocon, meta) = hoconWithMeta
        _ <- environmentFile(hocon, meta)
        _ <- environmentDocs(hocon, meta)
        _ <- docs(hocon, meta)
        _ = log.success("Hocones documentation generated")
      } yield ()).unsafeRunSync()
    }
  }

}
