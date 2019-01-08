package pl.onewebpro.hocones.meta.io

import cats.effect.{Resource, SyncIO}
import io.circe.Json
import io.circe.yaml.parser
import pl.onewebpro.hocones.meta.error.MetaError
import pl.onewebpro.hocones.meta.io.MetaFileWriter.MetaFile

import scala.io.Source

// TODO Refactor me plis
object MetaFileReader {

  // TODO make it better and move it to common
  private[io] def validateFile(metaFile: MetaFile): SyncIO[Unit] =
    if (metaFile.canRead)
      SyncIO.unit
    else
      SyncIO.raiseError(MetaError(s"File ${metaFile.getAbsoluteFile} is not readable."))

  def read(metaFile: MetaFile): SyncIO[Json] =
    for {
      _ <- validateFile(metaFile)
      text <- Resource
        .fromAutoCloseable(SyncIO(Source.fromFile(metaFile)))
        .use(source => SyncIO(source.getLines().mkString("\n")))
      parsedText <- if (text.nonEmpty) SyncIO.fromEither(parser.parse(text))
      else SyncIO.pure(Json.Null)
    } yield parsedText

}
