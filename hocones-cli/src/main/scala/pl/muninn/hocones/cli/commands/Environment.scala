package pl.muninn.hocones.cli.commands
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.Opts
import fansi.Color
import pl.muninn.hocones.cli.arguments.InputFile.InputFile
import pl.muninn.hocones.cli.arguments.environment.{RemoveDuplicates, WithComments, WithDefaults}
import pl.muninn.hocones.cli.arguments.{InputFile, OutputFile}
import pl.muninn.hocones.cli.commands.Hocones.HoconesCommand
import pl.muninn.hocones.cli.file.OutputFile.OutputFile
import pl.muninn.hocones.cli.file.{OutputFile => IOOutputFile}
import pl.muninn.hocones.env.EnvironmentFileGenerator
import pl.muninn.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.muninn.hocones.meta.model.MetaInformation
import pl.muninn.hocones.parser.HoconResult

object Environment {

  import pl.muninn.hocones.cli.show.showStr

  case class EnvironmentCommand(
    input: InputFile,
    output: Option[OutputFile],
    withComments: Boolean,
    withDefaults: Boolean,
    removeDuplicates: Boolean
  ) extends CliCommand

  object EnvironmentCommand {

    def fromCommand(command: CliCommand): EnvironmentCommand =
      command match {
        case HoconesCommand(input, _, withComments, withDefaults, removeDuplicates) =>
          EnvironmentCommand(
            input = input,
            output = None,
            withComments = withComments.getOrElse(false),
            withDefaults = withDefaults.getOrElse(false),
            removeDuplicates = removeDuplicates.getOrElse(false)
          )
        case _ =>
          EnvironmentCommand(
            input = command.input,
            output = None,
            withComments = false,
            withDefaults = false,
            removeDuplicates = false
          )
      }
  }

  val environmentCommandOpts: Opts[EnvironmentCommand] = (
    InputFile.opts,
    OutputFile.opts("environment file").orNone,
    WithComments.opts,
    WithDefaults.opts,
    RemoveDuplicates.opts
  ).mapN(EnvironmentCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand[CliCommand](name = "env-file", help = "generate environment file")(environmentCommandOpts)

  implicit private def mapCommandToConfig: EnvironmentCommand => EnvironmentConfiguration = { command =>
    EnvironmentConfiguration(
      outputPath = command.output
        .getOrElse(IOOutputFile.fromInputPath(command.input, ".env"))
        .toPath,
      withComments = command.withComments,
      withDefaults = command.withDefaults,
      removeDuplicates = command.removeDuplicates
    )
  }

  val environmentCommand: Kleisli[IO, (HoconResult, MetaInformation, EnvironmentCommand), Unit] =
    Kleisli {
      case (hocon, metaInformation, command) =>
        for {
          envConfiguration <- IO[EnvironmentConfiguration](command)
          _ <- putStrLn(Color.Green("Generating environment file"))
          _ <- EnvironmentFileGenerator.run(envConfiguration, hocon, metaInformation).toIO
          _ <- putStrLn(
            Color
              .Green("File generated ") ++ envConfiguration.outputPath.toFile.getAbsolutePath
          )
        } yield ()
    }
}
