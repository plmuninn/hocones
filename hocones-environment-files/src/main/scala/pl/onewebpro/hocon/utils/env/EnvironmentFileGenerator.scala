package pl.onewebpro.hocon.utils.env

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocon.utils.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocon.utils.env.model.ModelParser
import pl.onewebpro.hocon.utils.parser.HoconResult

object EnvironmentFileGenerator {

  import pl.onewebpro.hocon.utils.env.io._

  def apply(config: EnvironmentConfiguration, result: HoconResult): SyncIO[Unit] = for {
    outputFile <- SyncIO(tagOutputFile(config.outputPath.toFile))
    parentDirectory <- SyncIO(tagParentDirectory(config.outputPath.getParent.toFile))

    _ <- SyncIO.fromEither(OutputFileValidator.validate(outputFile, parentDirectory).leftMap(error => EnvironmentFileError(error.message)))

    writer <- SyncIO(new EnvironmentFileWriter(outputFile))
    values <- SyncIO(ModelParser.parse(config, result))

    _ <- writer.write(values)
  } yield ()
}
