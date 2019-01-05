package pl.onewebpro.hocones.env

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.common.io._
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.env.model.ModelGenerator
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

object EnvironmentFileGenerator {

  import pl.onewebpro.hocones.env.io._

  def apply(config: EnvironmentConfiguration, result: HoconResult, meta: MetaInformation): SyncIO[Unit] =
    for {
      outputFile <- SyncIO(tagOutputFile(config.outputPath.toFile))
      parentDirectory <- SyncIO(tagParentDirectory(config.outputPath.getParent.toFile))

      _ <- SyncIO.fromEither(
        OutputFileValidator
          .validate(outputFile, parentDirectory)
          .leftMap(error => EnvironmentFileError(error.message))
      )

      writer <- SyncIO(new EnvironmentFileWriter(outputFile))
      values <- SyncIO(ModelGenerator.generate(config, result, meta))

      _ <- writer.write(values)
    } yield ()
}
