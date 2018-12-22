package pl.onewebpro.hocones.cli

import cats.Show
import cats.effect.Console.io._
import cats.effect.{ExitCode, IO, IOApp, SyncIO}
import cats.implicits._
import com.monovore.decline.Help
import com.typesafe.config.ConfigFactory
import fansi.Str
import pl.onewebpro.hocones.cli.commands.Hocones
import pl.onewebpro.hocones.parser.HoconParser

object Main extends IOApp {

  implicit private val showStr: Show[Str] = Show.show(_.toString())

  private def displayHelpErrors: Help => IO[Unit] = { help =>
    IO.pure(help.errors)
      .flatMap(errors => errors.map(error => fansi.Color.Red(error)).map(str => putError(str)).sequence) *> IO.unit
  }

  private def displayHelp: Help => IO[Unit] = { help =>
    for {
      _ <- displayHelpErrors(help)
      _ <- IO(help.copy(errors = Nil)).flatMap(helpWithoutErrors => putStr(helpWithoutErrors.toString()))
    } yield ()
  }

  override def run(args: List[String]): IO[ExitCode] =
    SyncIO(Hocones.cmd.parse(args)).toIO.flatMap {
      case Left(help) => displayHelp(help) *> IO.pure(ExitCode.Error)
      case Right(inputFile) =>
        for {
          _ <- putStrLn(fansi.Color.Green("Loading hocon file"))
          _ <- HoconParser(ConfigFactory.parseFile(inputFile))
        } yield ExitCode.Success
    }

}
