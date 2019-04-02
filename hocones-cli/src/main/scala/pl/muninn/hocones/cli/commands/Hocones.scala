package pl.muninn.hocones.cli.commands

import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.{Command, Opts}
import com.typesafe.config.ConfigFactory
import fansi.Color
import pl.muninn.hocones.cli.arguments.InputFile
import pl.muninn.hocones.cli.arguments.InputFile.InputFile
import pl.muninn.hocones.cli.arguments.docs.TableAlignment
import pl.muninn.hocones.cli.arguments.environment.{RemoveDuplicates, WithComments, WithDefaults}
import pl.muninn.hocones.md.config.Configuration.{TableAlignment => MTableAlignment}
import pl.muninn.hocones.meta.MetaFile
import pl.muninn.hocones.meta.config.Configuration.MetaConfiguration
import pl.muninn.hocones.meta.model.MetaInformation
import pl.muninn.hocones.parser.{HoconParser, HoconResult}

object Hocones {

  import pl.muninn.hocones.cli.show.showStr

  case class HoconesCommand(
    input: InputFile,
    alignment: Option[MTableAlignment.TableAlignment],
    withComments: Option[Boolean],
    withDefaults: Option[Boolean],
    removeDuplicates: Option[Boolean]
  ) extends CliCommand

  val hoconesOpts: Opts[HoconesCommand] =
    (
      InputFile.opts,
      TableAlignment.opts.orNone,
      WithComments.opts.orNone,
      WithDefaults.opts.orNone,
      RemoveDuplicates.opts.orNone
    ).mapN(HoconesCommand.apply)

  private val commandF: Opts[CliCommand] =
    Statistics.cmd
      .orElse(Environment.cmd)
      .orElse(EnvironmentDocs.cmd)
      .orElse(Docs.cmd)
      .orElse(hoconesOpts)

  val cmd: Command[CliCommand] =
    Command(
      name = "hocones",
      header = "set of utilities for hocon format configuration files"
    )(commandF)

  val parse: Kleisli[IO, CliCommand, HoconResult] = Kleisli { command =>
    for {
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
          metaResult <- MetaFile.load(MetaConfiguration(input = command.input), result).toIO
          (metaFile, metaInformation) = metaResult
          _ <- putStrLn(Color.Green("Generated meta file ") ++ metaFile.getPath)
        } yield (result, metaInformation)
    }

  def parseAndLoadMetaInformation[T <: CliCommand]: Kleisli[IO, T, (HoconResult, MetaInformation)] =
    Kleisli { command =>
      parse(command).flatMap(result => metaInformation(command, result))
    }
}
