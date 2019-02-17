package pl.onewebpro.hocones.md

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.common.file._
import pl.onewebpro.hocones.md.config.Configuration.{DocumentConfiguration, TableConfiguration}
import pl.onewebpro.hocones.md.document.MarkdownDocumentation
import pl.onewebpro.hocones.md.document.renderer.document.DocumentRenderer
import pl.onewebpro.hocones.md.file.DocumentationWriter
import pl.onewebpro.hocones.md.table.EnvironmentTableGenerator
import pl.onewebpro.hocones.meta.document.model.Documentation
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

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

      table <- EnvironmentTableGenerator.generate(result, meta, documentation)
      text = table.md
      _ <- writer.write(text)
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
