package pl.muninn.hocones.meta

import io.circe.syntax._
import cats.effect.SyncIO
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pl.muninn.hocones.meta.config.Configuration.MetaConfiguration
import pl.muninn.hocones.meta.file.MetaFileWriter.MetaFile
import pl.muninn.hocones.meta.file.{MetaFileReader, MetaFileWriter}
import pl.muninn.hocones.parser.HoconResult
import pl.muninn.hocones.meta.BuildInfo.version
import pl.muninn.hocones.meta.model.MetaInformation
import pl.muninn.hocones.meta.model.JsonCodecs._

object MetaFile {

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

  def load(config: MetaConfiguration, hocones: HoconResult): SyncIO[(MetaFile, MetaInformation)] =
    for {
      logger <- Slf4jLogger.create[SyncIO]
      metaFile <- MetaFileWriter.create(config.input)
      _ <- logger.debug(s"Loaded meta file ${config.input.getAbsolutePath}")
      generatedMetaInformation <- MetaParser.generate(hocones)
      _ <- logger.debug("Generated meta information from hocon")
      result <- readMetaFile(metaFile)
      _ <- logger.debug("Meta file read")
      mergedMetaInformation <- MetaInformationMerger.merge(result, generatedMetaInformation)
      _ <- logger.debug("Default meta file merged with information from file")
      _ <- MetaFileWriter.printToFile(metaFile, mergedMetaInformation.asJson)
      _ <- logger.debug("Meta file wrote to meta file")
    } yield (metaFile, mergedMetaInformation)

}
