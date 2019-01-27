package pl.onewebpro.hocones.meta.file

import cats.implicits._
import cats.effect.{Resource, SyncIO}
import io.circe.Json
import io.circe.yaml.parser
import pl.onewebpro.hocones.common.file.{HoconFileUtils, InputFileValidator}
import pl.onewebpro.hocones.meta.error.MetaError
import pl.onewebpro.hocones.meta.file.MetaFileWriter.MetaFile

import scala.io.Source

object MetaFileReader {

  def read(metaFile: MetaFile): SyncIO[Json] =
    for {
      inputFile <- SyncIO.pure(HoconFileUtils.tagInputFile(metaFile))
      _ <- SyncIO.fromEither(InputFileValidator.validate(inputFile).leftMap(error => MetaError(error.message)))
      text <- Resource
        .fromAutoCloseable(SyncIO(Source.fromFile(metaFile)))
        .use(source => SyncIO(source.getLines().mkString("\n")))
      parsedText <- if (text.nonEmpty) SyncIO.fromEither(parser.parse(text)) else SyncIO.pure(Json.Null)
    } yield parsedText

}
