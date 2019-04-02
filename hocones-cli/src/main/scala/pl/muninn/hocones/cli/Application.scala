package pl.muninn.hocones.cli

import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import fansi.Color
import pl.muninn.hocones.cli.commands.EnvironmentDocs.EnvironmentDocsCommand
import pl.muninn.hocones.cli.commands.Environment.EnvironmentCommand
import pl.muninn.hocones.cli.commands.Docs.DocsCommand
import pl.muninn.hocones.cli.commands.{CliCommand, EnvironmentDocs, Environment, Docs, Hocones, Statistics}

object Application {

  import pl.muninn.hocones.cli.show.showStr
  import shapeless.syntax.std.tuple._

  val runStatisticsCommand: Kleisli[IO, commands.CliCommand, Unit] =
    Hocones.parse
      .andThen(Statistics.statisticsCommand)
      .andThen(Statistics.displayStatistics)

  val runEnvironmentCommand: Kleisli[IO, EnvironmentCommand, Unit] =
    Kleisli.ask[IO, EnvironmentCommand].flatMap { cmd =>
      Hocones.parseAndLoadMetaInformation
        .map(result => result :+ cmd)
        .andThen(Environment.environmentCommand)
    }

  val runEnvironmentDocsCommand: Kleisli[IO, EnvironmentDocsCommand, Unit] =
    Kleisli.ask[IO, EnvironmentDocsCommand].flatMap { cmd =>
      Hocones.parseAndLoadMetaInformation
        .map(result => result :+ cmd)
        .andThen(EnvironmentDocs.environmentDocsCommand)
    }

  val runDocsCommand: Kleisli[IO, DocsCommand, Unit] =
    Kleisli.ask[IO, DocsCommand].flatMap { cmd =>
      Hocones.parseAndLoadMetaInformation
        .map(result => result :+ cmd)
        .andThen(Docs.docsCommand)
    }

  val runFull: Kleisli[IO, CliCommand, Unit] =
    Kleisli.ask[IO, CliCommand].flatMapF { cmd =>
      for {
        _ <- putStrLn(Color.Green("Running full process"))

        parseAndMetaResult <- Hocones.parseAndLoadMetaInformation.run(cmd)
        (hocon, meta) = parseAndMetaResult

        _ <- Statistics.statisticsCommand
          .andThen(Statistics.displayStatistics)
          .run(hocon)

        environmentCommand <- IO(EnvironmentCommand.fromCommand(cmd))
        _ <- Environment.environmentCommand.run((hocon, meta, environmentCommand))

        docsCommand <- IO(DocsCommand.fromCommand(cmd))
        _ <- Docs.docsCommand.run((hocon, meta, docsCommand))

        environmentDocsCommand <- IO(EnvironmentDocsCommand.fromCommand(cmd))
        _ <- EnvironmentDocs.environmentDocsCommand.run((hocon, meta, environmentDocsCommand))

        _ <- putStrLn(Color.Green("Done. Bye bye!"))
      } yield ()
    }
}
