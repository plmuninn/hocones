package pl.onewebpro.hocones.meta

import io.circe.syntax._
import cats.effect.SyncIO
import pl.onewebpro.hocones.meta.config.Configuration.MetaConfiguration
import pl.onewebpro.hocones.meta.file.MetaFileWriter.MetaFile
import pl.onewebpro.hocones.meta.file.{MetaFileReader, MetaFileWriter}
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.meta.model.JsonCodecs._
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.meta.BuildInfo.version

object MetaGenerator {

  def defaultMetaInformation =
    MetaInformation(version, Map.empty, Nil)

  def readMetaFile(metaFile: MetaFile): SyncIO[MetaInformation] =
    MetaFileReader
      .read(metaFile)
      .flatMap(
        json =>
          if (json.isNull) SyncIO.pure(defaultMetaInformation)
          else SyncIO.fromEither(json.as[MetaInformation])
      )

  def apply(config: MetaConfiguration, hocones: HoconResult): SyncIO[(MetaFile, MetaInformation)] =
    for {
      metaFile <- MetaFileWriter.create(config.input)
      generatedMetaInformation <- MetaParser.generate(hocones)
      result <- readMetaFile(metaFile)
      mergedMetaInformation <- MetaInformationMerger.merge(result, generatedMetaInformation)
      _ <- MetaFileWriter.printToFile(metaFile, mergedMetaInformation.asJson)
    } yield (metaFile, mergedMetaInformation)

}
