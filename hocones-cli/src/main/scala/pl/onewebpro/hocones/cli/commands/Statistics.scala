package pl.onewebpro.hocones.cli.commands
import cats.Show
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import com.monovore.decline.Opts
import fansi.Color
import pl.onewebpro.hocones.cli.arguments.InputFile
import pl.onewebpro.hocones.cli.arguments.InputFile.InputFile
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.statistics.StatisticsMeta

object Statistics {

  import pl.onewebpro.hocones.cli.show.showStr

  case class StatisticsCommand(input: InputFile) extends CliCommand

  private val statisticsOpts: Opts[CliCommand] =
    InputFile.opts.map(StatisticsCommand.apply)

  implicit val showStatistics: Show[StatisticsMeta] = Show.show { statistics =>
    s"""
       |Number of paths: ${statistics.numOfPaths}
       |Number of environment values: ${statistics.numOfEnvironmentValues}
       |Number of not resolved references: ${statistics.numOfNotResolvedRef}
       |Number of resolved references: ${statistics.numOfResolvedRef}
           """.stripMargin
  }

  val cmd: Opts[CliCommand] =
    Opts.subcommand("statistics", "display statistics about configuration")(statisticsOpts)

  val displayStatistics: Kleisli[IO, StatisticsMeta, Unit] = Kleisli { statistics =>
    putStrLn(statistics)
  }

  val statisticsCommand: Kleisli[IO, HoconResult, StatisticsMeta] = Kleisli { hocon =>
    for {
      _ <- putStrLn(Color.Green("Loading statistics about configuration"))
      result <- StatisticsMeta.fromParsedHocon(hocon).toIO
    } yield result
  }
}
