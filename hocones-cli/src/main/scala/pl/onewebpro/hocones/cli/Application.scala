package pl.onewebpro.hocones.cli

import java.nio.file.Paths

import cats.effect.Console.io.putStrLn
import cats.effect.IO
import pl.onewebpro.hocones.env.EnvironmentFileGenerator
import pl.onewebpro.hocones.md.MdGenerator
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.statistics.StatisticsMeta

class Application(properties: Properties.CliProperties, parsedFile: HoconResult, meta: MetaInformation) {

  def statistics: IO[Unit] = for {
    _ <- putStrLn("Loading statistics about configuration")
    statistics <- StatisticsMeta.fromParsedHocon(parsedFile).toIO
    _ <- putStrLn(
      s"""
         |Number of paths: ${statistics.numOfPaths}
         |Number of environment values: ${statistics.numOfEnvironmentValues}
         |Number of not resolved references: ${statistics.numOfNotResolvedRef}
         |Number of resolved references: ${statistics.numOfResolvedRef}
           """.stripMargin)
  } yield ()

  def envFiles: IO[Unit] = for {
    _ <- putStrLn("Generating environment file")
    _ <- EnvironmentFileGenerator(properties.envConfiguration, parsedFile).toIO
    _ <- putStrLn(s"File generated ${properties.envConfiguration.outputPath}")
  } yield ()

  def environmentDocs: IO[Unit] = for {
    _ <- putStrLn("Generating documentation about environments")
    _ <- MdGenerator.generateTable(parsedFile, meta, properties.tableConfiguration).toIO
    _ <- putStrLn(s"File generated: ${properties.tableConfiguration.outputPath.toFile.getPath}")
  } yield ()

  def documentation: IO[Unit] = for {
    _ <- putStrLn("Generating documentation about configuration")
    _ <- MdGenerator.generateDocument(parsedFile, meta, properties.docsConfiguration).toIO
    _ <- putStrLn(s"File generated: ${properties.docsConfiguration.outputPath.toFile.getPath}")
  } yield ()

  def all: IO[Unit] = for {
    outputEnvFile <- IO(properties.input.getPath + ".env").map(path => Paths.get(path))
    environmentDocsFile <- IO(properties.input.getPath + ".environments.md").map(path => Paths.get(path))
    documentationFile <- IO(properties.input.getPath + ".md").map(path => Paths.get(path))

    config <- IO {
      properties.copy(
        envConfiguration = properties.envConfiguration.copy(outputPath = outputEnvFile),
        tableConfiguration = properties.tableConfiguration.copy(outputPath = environmentDocsFile),
        docsConfiguration = properties.docsConfiguration.copy(outputPath = documentationFile),
      )
    }

    _ <- putStrLn("Generating environment file")
    _ <- EnvironmentFileGenerator(config.envConfiguration, parsedFile).toIO
    _ <- putStrLn(s"File generated ${config.envConfiguration.outputPath}")

    _ <- putStrLn("Generating documentation about environments")
    _ <- MdGenerator.generateTable(parsedFile, meta, config.tableConfiguration).toIO
    _ <- putStrLn(s"File generated: ${config.tableConfiguration.outputPath.toFile.getPath}")

    _ <- putStrLn("Generating documentation about configuration")
    _ <- MdGenerator.generateDocument(parsedFile, meta, config.docsConfiguration).toIO
    _ <- putStrLn(s"File generated: ${config.docsConfiguration.outputPath.toFile.getPath}")

  } yield ()
}
