package pl.onewebpro.hocones.cli.commands
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import com.monovore.decline.{Command, Opts}
import com.typesafe.config.ConfigFactory
import fansi.Color
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pl.onewebpro.hocones.cli.arguments.InputFile
import pl.onewebpro.hocones.cli.arguments.InputFile.InputFile
import pl.onewebpro.hocones.meta.MetaGenerator
import pl.onewebpro.hocones.meta.config.Configuration.MetaConfiguration
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.{HoconParser, HoconResult}

object Hocones {

  import pl.onewebpro.hocones.cli.show.showStr

  case class HoconesCommand(input: InputFile) extends CliCommand

  private val hoconesF: Opts[HoconesCommand] =
    InputFile.opts.map(HoconesCommand.apply)

  private val commandF: Opts[CliCommand] =
    Statistics.cmd
      .orElse(Environment.cmd)
      .orElse(EnvironmentDocs.cmd)
      .orElse(Docs.cmd)
      .orElse(hoconesF)

  val cmd: Command[CliCommand] =
    Command(
      name = "hocones",
      header = "set of utilities for hocon format configuration files"
    )(commandF)

  val parse: Kleisli[IO, CliCommand, HoconResult] = Kleisli { command =>
    for {
      logger <- Slf4jLogger.create[IO]
      _ <- putStrLn(Color.Green("Loading hocon file"))
      result <- HoconParser(ConfigFactory.parseFile(command.input))
      _ <- putStrLn(Color.Green("Configuration parsed without errors"))
    } yield result
  }

  val metaInformation: Kleisli[IO, (CliCommand, HoconResult), (HoconResult, MetaInformation)] =
    Kleisli {
      case (command, result) =>
        for {
          _ <- putStrLn(Color.Green("Generating file with meta information"))
          metaResult <- MetaGenerator(MetaConfiguration(input = command.input), result).toIO
          (metaFile, metaInformation) = metaResult
          _ <- putStrLn(Color.Green("Generated meta file ") ++ metaFile.getPath)
        } yield (result, metaInformation)
    }

  val parseAndLoadMetaInformation: Kleisli[IO, CliCommand, (HoconResult, MetaInformation)] =
    Kleisli { command =>
      parse(command).flatMap(result => metaInformation(command, result))
    }
}
