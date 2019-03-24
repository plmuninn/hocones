import com.typesafe.config.ConfigFactory
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
  lazy val ignoredSystemPaths = Set(
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

  lazy val popularIgnoredPaths = Set(
    "akka",
    "spark",
    "slick"
  )

  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = JvmPlugin

  object autoImport extends HoconesKeys

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    forPath := None,
    ignoredPaths := popularIgnoredPaths,
    configFileToLoad := None,
    pathForSave := None,
    includeConfigsFromDependencies := true,
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

  private lazy val internalClassLoaderTask = Def.task {
    ClasspathUtilities.toLoader(Attributed.data((Compile / unmanagedClasspath).value))
  }

  private lazy val dependencyClassLoaderTask = Def.task {
    ClasspathUtilities.toLoader(Attributed.data((Compile / dependencyClasspath).value))
  }

  private lazy val classLoaderTask = Def.taskDyn {
    includeConfigsFromDependencies.flatMap {
      case true  => dependencyClassLoaderTask.taskValue
      case false => internalClassLoaderTask.taskValue
    }
  }

  private lazy val loadedConfigTask = Def.taskDyn {
    val classLoader = classLoaderTask.value

    configFileToLoad
      .map({
        case Some(file) => ConfigFactory.parseFile(file)
        case None       => ConfigFactory.load(classLoader)
      })
  }

  private lazy val configTask = Def.taskDyn {
    loadedConfigTask.map { config =>
      (ignoredSystemPaths ++ ignoredPaths.value).foldLeft(config.root) {
        case (cfg, path) => cfg.withoutKey(path)
      }
    }
  }

  private lazy val inputFileNameTask = Def.taskDyn {
    configFileToLoad.map(_.getOrElse(new File("application.conf")))
  }

  lazy val inputPathTask = Def.taskDyn {
    val fileToLoad = configFileToLoad.value
    val pathName = inputFileNameTask.value.getName
    val classLoader = classLoaderTask.value

    Def.task {
      fileToLoad match {
        case Some(pathToSave) => Paths.get(pathToSave + s"/$pathName")
        case None             => Paths.get(classLoader.getResource(pathName).getPath)
      }
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
      println(loadedConfig.render())


      val loadWithMeta: IO[(HoconResult, model.MetaInformation)] =
        for {
          result <- HoconParser(loadedConfig.toConfig)
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
          _ <- EnvironmentFileGenerator.run(config, hocon, meta).toIO
        } yield ()
        else IO.unit

      val environmentDocs: (HoconResult, MetaInformation) => IO[Unit] = (hocon: HoconResult, meta: MetaInformation) =>
        if (createEnvironmentDocsFileSetting) for {
          _ <- IO.unit
          tableConfig = TableConfiguration(getOutputPath(".env.md"), tableAlignmentTaskSetting)
          documentation <- GenerateDocumentation.generate(hocon, meta).toIO
          _ <- MdGenerator
            .generateTable(hocon, meta, documentation, tableConfig)
            .toIO
        } yield ()
        else IO.unit

      val docs: (HoconResult, MetaInformation) => IO[Unit] = (hocon: HoconResult, meta: MetaInformation) =>
        if (createDocumentationFileSetting) for {
          _ <- IO.unit
          documentConfiguration = DocumentConfiguration(getOutputPath(".env.md"))
          documentation <- GenerateDocumentation.generate(hocon, meta).toIO
          _ <- MdGenerator
            .generateDocument(hocon, documentation, documentConfiguration)
            .toIO
        } yield ()
        else IO.unit

      try {
        (for {
          hoconWithMeta <- loadWithMeta
          (hocon, meta) = hoconWithMeta
          _ <- environmentFile(hocon, meta)
          _ <- environmentDocs(hocon, meta)
          _ <- docs(hocon, meta)
        } yield ()).unsafeRunSync()
      } catch {
        case error: Throwable =>
          println(error.getMessage)
          throw error
      }
    }
  }

}
