package pl.muninn.hocones.meta.file

import cats.implicits._
import cats.effect.{Resource, SyncIO}
import io.circe.Json
import io.circe.yaml.parser
import pl.muninn.hocones.common.file.{tagInputFile, InputFileValidator}
import pl.muninn.hocones.meta.error.MetaError
import pl.muninn.hocones.meta.file.MetaFileWriter.MetaFile

import scala.io.Source

object MetaFileReader {

  def read(metaFile: MetaFile): SyncIO[Json] =
    for {
      inputFile <- SyncIO.pure(tagInputFile(metaFile))
      _ <- SyncIO.fromEither(InputFileValidator.validate(inputFile).leftMap(error => MetaError(error.message)))
      text <- Resource
        .fromAutoCloseable(SyncIO(Source.fromFile(metaFile)))
        .use(source => SyncIO(source.getLines().mkString("\n")))
      parsedText <- if (text.nonEmpty) SyncIO.fromEither(parser.parse(text)) else SyncIO.pure(Json.Null)
    } yield parsedText

}
