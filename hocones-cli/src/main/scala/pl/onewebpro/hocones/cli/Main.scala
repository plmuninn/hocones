package pl.onewebpro.hocones.cli

import cats.effect.Console.io._
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import pl.onewebpro.hocones.cli.Properties.ProgramMode
import pl.onewebpro.hocones.meta.MetaGenerator
import pl.onewebpro.hocones.meta.config.Configuration.MetaConfiguration
import pl.onewebpro.hocones.parser.HoconParser

object Main extends IOApp {

  def runApp(properties: Properties.CliProperties,
             application: Application): IO[Unit] =
    properties.mode match {
      case ProgramMode.Default    => application.all
      case ProgramMode.EnvFile    => application.envFiles
      case ProgramMode.Statistics => application.statistics
      case ProgramMode.EnvDocs    => application.environmentDocs
      case ProgramMode.Docs       => application.documentation
    }

  override def run(args: List[String]): IO[ExitCode] =
    Properties.parseArgs(args).toIO.flatMap {
      case Right(properties) =>
        for {
          _ <- putStrLn("Loading configurations")
          parsedFile <- HoconParser(ConfigFactory.parseFile(properties.input))
          _ <- putStrLn("")
          _ <- putStrLn("Configuration parsed without errors")
          _ <- putStrLn("Generating file with meta information")
          result <- MetaGenerator(MetaConfiguration(input = properties.input),
                                  parsedFile).toIO
          (metaFile, metaInformation) = result
          _ <- putStrLn(s"Generated meta file ${metaFile.getPath}")
          application = new Application(properties, parsedFile, metaInformation)
          _ <- runApp(properties, application)
          _ <- putStrLn("Done. Bye bye!")
        } yield ExitCode.Success
      case Left(_) => IO.pure(ExitCode.Error)
    }

}
