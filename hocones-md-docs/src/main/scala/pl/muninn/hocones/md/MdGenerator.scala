package pl.muninn.hocones.md

import cats.effect.SyncIO
import cats.implicits._
import pl.muninn.hocones.common.file._
import pl.muninn.hocones.md.config.Configuration.{DocumentConfiguration, TableConfiguration}
import pl.muninn.hocones.md.document.MarkdownDocumentation
import pl.muninn.hocones.md.document.renderer.document.DocumentRenderer
import pl.muninn.hocones.md.file.DocumentationWriter
import pl.muninn.hocones.md.table.{EnvironmentTableElement, EnvironmentTableGenerator}
import pl.muninn.hocones.meta.document.model.Documentation
import pl.muninn.hocones.meta.model.MetaInformation
import pl.muninn.hocones.parser.HoconResult

object MdGenerator {

  def generateTable(
    result: HoconResult,
    meta: MetaInformation,
    documentation: Documentation,
    config: TableConfiguration
  ): SyncIO[Unit] =
    for {
      outputFile <- SyncIO(tagOutputFile(config.outputPath.toFile))
      parentDirectory <- SyncIO(tagParentDirectory(config.outputPath.getParent.toFile))

      _ <- SyncIO.fromEither(
        OutputFileValidator
          .validate(outputFile, parentDirectory)
          .leftMap(error => MdFileError(error.message))
      )

      writer = new DocumentationWriter(outputFile)
      values <- EnvironmentTableElement.generate(result, meta, documentation)
      _ <- if (values.nonEmpty) {
        for {
          _ <- SyncIO.unit
          table = EnvironmentTableGenerator.generateTable(values)
          text = table.md
          _ <- writer.write(text)
        } yield ()
      } else SyncIO.unit
    } yield ()

  def generateDocument(result: HoconResult, documentation: Documentation, config: DocumentConfiguration): SyncIO[Unit] =
    for {
      outputFile <- SyncIO(tagOutputFile(config.outputPath.toFile))
      parentDirectory <- SyncIO(tagParentDirectory(config.outputPath.getParent.toFile))

      _ <- SyncIO.fromEither(
        OutputFileValidator
          .validate(outputFile, parentDirectory)
          .leftMap(error => MdFileError(error.message))
      )

      writer = new DocumentationWriter(outputFile)
      markdownDocumentation <- MarkdownDocumentation.fromDocumentation(documentation)(DocumentRenderer.renderer)
      text = markdownDocumentation.md
      _ <- writer.write(text)
    } yield ()

  def generate(
    result: HoconResult,
    meta: MetaInformation,
    documentation: Documentation,
    configuration: Either[TableConfiguration, DocumentConfiguration]
  ): SyncIO[Unit] =
    configuration match {
      case Left(tableConfiguration) =>
        generateTable(result, meta, documentation, tableConfiguration)
      case Right(documentConfiguration) =>
        generateDocument(result, documentation, documentConfiguration)
    }

}
