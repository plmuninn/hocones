package pl.onewebpro.hocones.env

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.common.file._
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.env.model.ModelGenerator
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object EnvironmentFileGenerator {

  type Command = (EnvironmentConfiguration, HoconResult, MetaInformation)

  import pl.onewebpro.hocones.env.file._

  val tagFiles: EnvironmentConfiguration => SyncIO[(OutputFile, ParentDirectory)] = { config =>
    for {
      logger <- Slf4jLogger.create[SyncIO]
      _ <- logger.debug(s"Logging output file ${config.outputPath.toFile.getAbsolutePath}")
      outputFile <- SyncIO(config.outputPath.toFile)
      _ <- logger.debug(s"Logging parent file${config.outputPath.getParent.toAbsolutePath}")
      parentFile <- SyncIO(config.outputPath.getParent.toFile)
      _ <- logger.debug("Tagging done")
    } yield (tagOutputFile(outputFile), tagParentDirectory(parentFile))
  }

  private[env] def compute: Command => SyncIO[Unit] = {
    case (config, result, meta) =>
      for {
        logger <- Slf4jLogger.create[SyncIO]
        taggingResult <- tagFiles(config)
        (outputFile, parentDirectory) = taggingResult

        _ <- logger.debug("Validating paths")

        _ <- SyncIO.fromEither(
          OutputFileValidator
            .validate(outputFile, parentDirectory)
            .leftMap(error => EnvironmentFileError(error.message))
        )

        _ <- logger.debug("Creating environment file writer")
        writer <- SyncIO(new EnvironmentFileWriter(outputFile))

        _ <- logger.debug("Generating list of environment files")
        values <- SyncIO(ModelGenerator.generate(config, result, meta))
        _ <- logger.debug("Values generated:\n" + values.mkString(";\n"))

        _ <- logger.debug("Writing values to file")
        _ <- writer.write(values)
      } yield ()
  }

  def run: Command => SyncIO[Unit] =
    compute(_).adaptError {
      case environmentFileError: EnvironmentFileError => environmentFileError
      case error: Throwable                           => EnvironmentFileError(error.getMessage, error)
    }

}
