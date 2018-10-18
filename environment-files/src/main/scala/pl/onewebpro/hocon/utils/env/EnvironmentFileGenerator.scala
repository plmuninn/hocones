package pl.onewebpro.hocon.utils.env

import cats.effect.IO
import cats.implicits._
import pl.onewebpro.hocon.utils.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocon.utils.env.model.ModelParser
import pl.onewebpro.hocon.utils.parser.HoconResult

object EnvironmentFileGenerator {

  import pl.onewebpro.hocon.utils.env.io._

  def apply(config: EnvironmentConfiguration, result: HoconResult): IO[Unit] = for {
    outputFile <- IO(tagOutputFile(config.outputPath.toFile))
    parentDirectory <- IO(tagParentDirectory(config.outputPath.getParent.toFile))

    _ <- IO.fromEither(OutputFileValidator.validate(outputFile, parentDirectory).leftMap(error => EnvironmentFileError(error.message)))

    writer <- IO(new EnvironmentFileWriter(outputFile))
    values <- IO(ModelParser(config, result))

    _ <- writer.write(values)
  } yield ()
}
