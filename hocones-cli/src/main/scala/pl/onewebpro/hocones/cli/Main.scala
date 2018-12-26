package pl.onewebpro.hocones.cli

import cats.data.Kleisli
import cats.effect.Console.io._
import cats.effect.{ExitCode, IO, IOApp, SyncIO}
import cats.implicits._
import com.monovore.decline.Help
import fansi.Color
import pl.onewebpro.hocones.cli.commands.Docs.DocsCommand
import pl.onewebpro.hocones.cli.commands.Environment.EnvironmentCommand
import pl.onewebpro.hocones.cli.commands.EnvironmentDocs.EnvironmentDocsCommand
import pl.onewebpro.hocones.cli.commands.Statistics.StatisticsCommand
import pl.onewebpro.hocones.cli.commands._

object Main extends IOApp {

  import pl.onewebpro.hocones.cli.show.showStr
  import shapeless.syntax.std.tuple._

  private def displayHelpErrors: Help => IO[Unit] = { help =>
    IO.pure(help.errors)
      .flatMap(errors => errors.map(error => Color.Red(error)).map(str => putError(str)).sequence) *> IO.unit
  }

  private def displayHelp: Help => IO[Unit] = { help =>
    for {
      _ <- displayHelpErrors(help)
      _ <- IO(help.copy(errors = Nil)).flatMap(helpWithoutErrors => putStrLn(helpWithoutErrors.toString()))
    } yield ()
  }

  val runStatisticsCommand: Kleisli[IO, commands.CliCommand, Unit] = {
    import Statistics.showStatistics

    Hocones.parse.andThen(Statistics.statisticsCommand).map(statistics => putStr(Color.Green(statistics.show)))
  }

  val runEnvironmentCommand: EnvironmentCommand => Kleisli[IO, commands.CliCommand, Unit] = { cmd =>
    Hocones.parse
      .map(result => (result, cmd))
      .andThen(Environment.environmentCommand)
  }

  val runEnvironmentDocsCommand: EnvironmentDocsCommand => Kleisli[IO, commands.CliCommand, Unit] = { cmd =>
    Hocones.parseAndLoadMetaInformation
      .map(result => result :+ cmd)
      .andThen(EnvironmentDocs.environmentDocsCommand)
  }

  val runDocsCommand: DocsCommand => Kleisli[IO, commands.CliCommand, Unit] = { cmd =>
    Hocones.parseAndLoadMetaInformation
      .map(result => result :+ cmd)
      .andThen(Docs.docsCommand)
  }

  val runFull: CliCommand => IO[Unit] = { cmd =>
    for {
      _ <- putStrLn(Color.Green("Running full process"))

      parseAndMetaResult <- Hocones.parseAndLoadMetaInformation.run(cmd)
      (hocon, meta) = parseAndMetaResult

      _ <- Statistics.statisticsCommand.run(hocon)

      environmentCommand <- IO(EnvironmentCommand.fromCommand(cmd))
      _ <- Environment.environmentCommand.run((hocon, environmentCommand))

      environmentDocsCommand <- IO(EnvironmentDocsCommand.fromCommand(cmd))
      _ <- EnvironmentDocs.environmentDocsCommand.run((hocon, meta, environmentDocsCommand))

      docsCommand <- IO(DocsCommand.fromCommand(cmd))
      _ <- Docs.docsCommand.run((hocon, meta, docsCommand))

      _ <- putStrLn(Color.Green("Done. Bye bye!"))
    } yield ()
  }

  override def run(args: List[String]): IO[ExitCode] =
    SyncIO(Hocones.cmd.parse(args)).toIO.flatMap {
      case Left(help) =>
        (displayHelp(help) *> IO.pure(ExitCode.Error))
          .handleErrorWith(ErrorHandler.handler)

      case Right(cmd: StatisticsCommand) =>
        (runStatisticsCommand.run(cmd) *> IO.pure(ExitCode.Success))
          .handleErrorWith(ErrorHandler.handler)

      case Right(cmd: EnvironmentCommand) =>
        (runEnvironmentCommand(cmd).run(cmd) *> IO.pure(ExitCode.Success))
          .handleErrorWith(ErrorHandler.handler)

      case Right(cmd: EnvironmentDocsCommand) =>
        (runEnvironmentDocsCommand(cmd).run(cmd) *> IO.pure(ExitCode.Success))
          .handleErrorWith(ErrorHandler.handler)

      case Right(cmd: DocsCommand) =>
        (runDocsCommand(cmd).run(cmd) *> IO.pure(ExitCode.Success))
          .handleErrorWith(ErrorHandler.handler)

      case Right(cmd: CliCommand) =>
        (runFull(cmd) *> IO.pure(ExitCode.Success))
          .handleErrorWith(ErrorHandler.handler)
    }

}
