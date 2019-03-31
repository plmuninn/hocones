package pl.muninn.hocones.cli

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp, SyncIO}
import cats.implicits._
import pl.muninn.hocones.cli.commands.Docs.DocsCommand
import pl.muninn.hocones.cli.commands.Environment.EnvironmentCommand
import pl.muninn.hocones.cli.commands.EnvironmentDocs.EnvironmentDocsCommand
import pl.muninn.hocones.cli.commands.{CliCommand, Hocones}
import pl.muninn.hocones.cli.commands.Statistics.StatisticsCommand

object Main extends IOApp {

  import Application._
  import ApplicationHelp._

  implicit class CommandOps[T](fn: Kleisli[IO, T, Unit]) {

    def safeRun(cmd: T): IO[ExitCode] =
      (fn.run(cmd) *> IO.pure(ExitCode.Error))
        .handleErrorWith(ErrorHandler.handler)
  }

  override def run(args: List[String]): IO[ExitCode] =
    SyncIO(Hocones.cmd.parse(args)).toIO.flatMap {
      case Left(help)                         => displayHelp.safeRun(help)
      case Right(cmd: StatisticsCommand)      => runStatisticsCommand.safeRun(cmd)
      case Right(cmd: EnvironmentCommand)     => runEnvironmentCommand.safeRun(cmd)
      case Right(cmd: EnvironmentDocsCommand) => runEnvironmentDocsCommand.safeRun(cmd)
      case Right(cmd: DocsCommand)            => runDocsCommand.safeRun(cmd)
      case Right(cmd: CliCommand)             => runFull.safeRun(cmd)
    }

}
