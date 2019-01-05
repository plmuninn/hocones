package pl.onewebpro.hocones.md

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.common.io._
import pl.onewebpro.hocones.md.config.Configuration.{DocumentConfiguration, TableConfiguration}
import pl.onewebpro.hocones.md.document.DocumentationGenerator
import pl.onewebpro.hocones.md.io.DocumentationWriter
import pl.onewebpro.hocones.md.table.{EnvironmentTable, EnvironmentTableGenerator}
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

object MdGenerator {

  def generateTable(result: HoconResult, meta: MetaInformation, config: TableConfiguration) =
    for {
      outputFile <- SyncIO(tagOutputFile(config.outputPath.toFile))
      parentDirectory <- SyncIO(tagParentDirectory(config.outputPath.getParent.toFile))

      _ <- SyncIO.fromEither(
        OutputFileValidator
          .validate(outputFile, parentDirectory)
          .leftMap(error => MdFileError(error.message))
      )

      table = new EnvironmentTable(config)
      writer = new DocumentationWriter(outputFile)

      rows <- EnvironmentTableGenerator(result, meta)
      text <- table.fromRows(rows)
      _ <- writer.write(text)
    } yield ()

  def generateDocument(result: HoconResult, meta: MetaInformation, config: DocumentConfiguration) =
    for {
      outputFile <- SyncIO(tagOutputFile(config.outputPath.toFile))
      parentDirectory <- SyncIO(tagParentDirectory(config.outputPath.getParent.toFile))

      _ <- SyncIO.fromEither(
        OutputFileValidator
          .validate(outputFile, parentDirectory)
          .leftMap(error => MdFileError(error.message))
      )

      writer = new DocumentationWriter(outputFile)
      documentation <- DocumentationGenerator(result, meta)
      text <- documentation.toMd
      _ <- writer.write(text)
    } yield ()

  def generate(
    result: HoconResult,
    meta: MetaInformation,
    configuration: Either[TableConfiguration, DocumentConfiguration]
  ) =
    configuration match {
      case Left(tableConfiguration) =>
        generateTable(result, meta, tableConfiguration)
      case Right(documentConfiguration) =>
        generateDocument(result, meta, documentConfiguration)
    }

}
