package pl.onewebpro.hocones.cli

import java.io.File

import cats.effect.SyncIO
import com.typesafe.config.ConfigFactory
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.cli.Properties.ProgramMode.ProgramMode
import pl.onewebpro.hocones.md.config.Configuration.{
  DocumentConfiguration,
  TableAlignment,
  TableConfiguration
}

import scala.util.Try

object Properties {

  object ProgramMode extends Enumeration {
    type ProgramMode = Value

    val Default, Statistics, EnvFile, EnvDocs, Docs = Value
  }

  case class CliProperties(
      input: File = null,
      mode: ProgramMode = ProgramMode.Default,
      envConfiguration: EnvironmentConfiguration = EnvironmentConfiguration(
        outputPath = null,
        withComments = true,
        withDefaults = true,
        removeDuplicates = true),
      tableConfiguration: TableConfiguration = TableConfiguration(
        outputPath = null,
        aligned = TableAlignment.Left
      ),
      docsConfiguration: DocumentConfiguration = DocumentConfiguration(
        outputPath = null
      ))

  val unit: Unit = ()

  private def validateFile: File => Either[String, Unit] = { file =>
    Either.cond(file.exists(), unit, s"File ${file.getAbsolutePath} not exist")
  }

  private def validateHocon: File => Either[String, Unit] = { file =>
    for {
      _ <- Either.cond(file.canRead,
                       unit,
                       s"File ${file.getAbsolutePath} is not readable")
      _ <- Either.cond(Try(ConfigFactory.parseFile(file)).isSuccess,
                       unit,
                       s"File ${file.getAbsolutePath} is not proper hocon file")
    } yield ()
  }

  private implicit val tableAlignmentRead: scopt.Read[TableAlignment.Value] =
    scopt.Read.reads(TableAlignment.withName)

  lazy val parser =
    new scopt.OptionParser[CliProperties]("hocones") {
      head("hocones", "0.1")
      note("set of utilities for hocon format configurations \n")

      opt[File]('i', "input")
        .required()
        .valueName("<file>")
        .validate(validateFile)
        .validate(validateHocon)
        .action((input, cfg) => cfg.copy(input = input))
        .text("input is a required file property - it needs to be file containing hocon type of configuration")

      note(
        "\nfor default, application will load hocon file, generate meta information, generate env file and documentation \n")

      cmd("statistics")
        .text("display statistics about configuration")

      cmd("env-file")
        .action((_, cfg) => cfg.copy(mode = ProgramMode.EnvFile))
        .text("generate environment file")
        .children(
          opt[File]('o', "output")
            .required()
            .valueName("<file>")
            .action((output, cfg) =>
              cfg.copy(envConfiguration =
                cfg.envConfiguration.copy(outputPath = output.toPath)))
            .text("output is a required file property - file for saving environment values"),
          opt[Boolean]('c', "comments")
            .optional()
            .withFallback(() => true)
            .action((comments, cfg) =>
              cfg.copy(envConfiguration =
                cfg.envConfiguration.copy(withComments = comments)))
            .text("comments is boolean property - should comments about environment variables be printed in file - default true"),
          opt[Boolean]('d', "defaults")
            .optional()
            .withFallback(() => true)
            .action((defaults, cfg) =>
              cfg.copy(envConfiguration =
                cfg.envConfiguration.copy(withDefaults = defaults)))
            .text("defaults is boolean property - should default values of environment variables be set in file - default true"),
          opt[Boolean]('r', "remove-duplicates")
            .optional()
            .withFallback(() => true)
            .action((duplicates, cfg) =>
              cfg.copy(envConfiguration =
                cfg.envConfiguration.copy(removeDuplicates = duplicates)))
            .text("remove-duplicates is boolean property - should duplicates be removed from output file - default true")
        )

      cmd("env-docs")
        .action((_, cfg) => cfg.copy(mode = ProgramMode.EnvDocs))
        .text("generate md table with environments")
        .children(
          opt[File]('o', "output")
            .required()
            .valueName("<file>")
            .action((output, cfg) =>
              cfg.copy(tableConfiguration =
                cfg.tableConfiguration.copy(outputPath = output.toPath)))
            .text("output is a required file property - file for saving documentation"),
          opt[TableAlignment.Value]('a', "alignment")
            .withFallback(() => TableAlignment.Left)
            .action((alignment, cfg) =>
              cfg.copy(tableConfiguration =
                cfg.tableConfiguration.copy(aligned = alignment)))
            .text("alignment of values in table (Left, Right, Center) - default Left"),
        )

      cmd("docs")
        .action((_, cfg) => cfg.copy(mode = ProgramMode.Docs))
        .text("generate md table with environments")
        .children(
          opt[File]('o', "output")
            .required()
            .valueName("<file>")
            .action((output, cfg) =>
              cfg.copy(docsConfiguration =
                cfg.docsConfiguration.copy(outputPath = output.toPath)))
            .text("output is a required file property - file for saving documentation"),
        )
    }

  def parseArgs(args: List[String]): SyncIO[Either[Unit, CliProperties]] =
    SyncIO(parser.parse(args, CliProperties())).flatMap {
      case Some(properties) => SyncIO.pure(Right(properties))
      case _                => SyncIO.pure(Left(unit))
    }
}
