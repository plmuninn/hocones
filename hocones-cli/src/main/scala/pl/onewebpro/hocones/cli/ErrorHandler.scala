package pl.onewebpro.hocones.cli

import cats.implicits._
import cats.effect.Console.io.putStrLn
import cats.effect.{ExitCode, IO}
import fansi.Color
import _root_.io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import _root_.io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import pl.onewebpro.hocones.env.EnvironmentFileError
import pl.onewebpro.hocones.md.MdFileError
import pl.onewebpro.hocones.meta.{MetaError, MetaParsingError}
import pl.onewebpro.hocones.parser.ParsingError

object ErrorHandler {

  import pl.onewebpro.hocones.cli.show.showStr

  val errorStatus: IO[ExitCode] = IO.pure(ExitCode.Error)
  val loggerIO: IO[SelfAwareStructuredLogger[IO]] = Slf4jLogger.create[IO]

  val handleUnexpectedError: Throwable => IO[Unit] = { error =>
    for {
      logger <- loggerIO
      _ <- logger.error(error)("Unexpected error")
      _ <- putStrLn(Color.Red("Unexpected error"))
    } yield ()
  }

  val handleParsingError: ParsingError => IO[Unit] = { error =>
    for {
      logger <- loggerIO
      _ <- logger.error(error)("Parsing error")
      _ <- putStrLn(Color.Red(s"Error during parsing process of configuration file: ${error.getMessage}"))
    } yield ()
  }

  val handleEnvironmentFileError: EnvironmentFileError => IO[Unit] = { error =>
    for {
      logger <- loggerIO
      _ <- logger.error(error)("Generating environment file error")
      _ <- putStrLn(Color.Red(s"Error during generation of environment file: ${error.getMessage}"))
    } yield ()
  }

  val handleMdFileError: MdFileError => IO[Unit] = { error =>
    for {
      logger <- loggerIO
      _ <- logger.error(error)("Generating md file error")
      _ <- putStrLn(Color.Red(s"Error during generation of markdown file: ${error.message}"))
    } yield ()
  }

  val handleMetaParsingError: MetaParsingError => IO[Unit] = { error =>
    for {
      logger <- loggerIO
      _ <- logger.error(error)("Meta information parsing error")
      _ <- putStrLn(Color.Red(s"Error during parsing meta information file: ${error.getMessage}"))
    } yield ()
  }

  val handleMetaError: MetaError => IO[Unit] = { error =>
    for {
      logger <- loggerIO
      _ <- logger.error(error)("Meta error")
      _ <- putStrLn(Color.Red(s"Error during meta information processing: ${error.getMessage}"))
    } yield ()
  }

  val handler: Throwable => IO[ExitCode] = {
    case error: ParsingError         => handleParsingError(error) *> errorStatus
    case error: EnvironmentFileError => handleEnvironmentFileError(error) *> errorStatus
    case error: MdFileError          => handleMdFileError(error) *> errorStatus
    case error: MetaParsingError     => handleMetaParsingError(error) *> errorStatus
    case error: MetaError            => handleMetaError(error) *> errorStatus
    case error: Throwable            => handleUnexpectedError(error) *> errorStatus
  }

}
