package pl.onewebpro.hocones.cli

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import pl.onewebpro.hocones.parser.{HoconParser, HoconResult}
import pl.onewebpro.hocones.cli.Properties.ProgramMode
import pl.onewebpro.hocones.statistics.StatisticsMeta
import cats.effect.Console.io._
import pl.onewebpro.hocones.env.EnvironmentFileGenerator
import pl.onewebpro.hocones.md.MdGenerator
import pl.onewebpro.hocones.meta.MetaGenerator
import pl.onewebpro.hocones.meta.config.Configuration.MetaConfiguration
import pl.onewebpro.hocones.meta.model.MetaInformation

object Main extends IOApp {

  def runApp(properties: Properties.CliProperties, parsedFile: HoconResult, meta: MetaInformation): IO[Unit] =
    properties.mode match {
      case ProgramMode.EnvFile => for {
        _ <- putStrLn("Generating environment file")
        _ <- EnvironmentFileGenerator(properties.envConfiguration, parsedFile).toIO
        _ <- putStrLn(s"File generated ${properties.envConfiguration.outputPath}")
      } yield ()
      case ProgramMode.Load => for {
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
      case ProgramMode.EnvDocs => for {
        _ <- putStrLn("Generating documentation about environments")
        _ <- MdGenerator.generateTable(parsedFile, meta, properties.tableConfiguration).toIO
        _ <- putStrLn(s"File generated: ${properties.tableConfiguration.outputPath.toFile.getPath}")
      } yield ()
      case ProgramMode.Docs => for {
        _ <- putStrLn("Generating documentation about configuration")
        _ <- MdGenerator.generateDocument(parsedFile, meta, properties.docsConfiguration).toIO
        _ <- putStrLn(s"File generated: ${properties.docsConfiguration.outputPath.toFile.getPath}")
      } yield ()
    }

  override def run(args: List[String]): IO[ExitCode] =
    Properties.parseArgs(args).toIO.flatMap {
      case Right(properties) => for {
        _ <- putStrLn("Loading configurations")
        parsedFile <- HoconParser(ConfigFactory.parseFile(properties.input))
        _ <- putStrLn("Configuration parsed without errors")
        _ <- putStrLn("Generating file with meta information")
        result <- MetaGenerator(MetaConfiguration(input = properties.input), parsedFile).toIO
        (metaFile, metaInformation) = result
        _ <- putStrLn(s"Generated meta file ${metaFile.getAbsoluteFile}")
        _ <- runApp(properties, parsedFile, metaInformation)
        _ <- putStrLn("Done. Bye bye!")
      } yield ExitCode.Success
      case Left(_) => IO.pure(ExitCode.Error)
    }

}
