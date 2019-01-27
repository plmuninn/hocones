package pl.onewebpro.hocones.cli.commands
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.Opts
import fansi.Color
import pl.onewebpro.hocones.cli.arguments.InputFile.InputFile
import pl.onewebpro.hocones.cli.arguments.{InputFile, OutputFile}
import pl.onewebpro.hocones.cli.file.OutputFile.OutputFile
import pl.onewebpro.hocones.cli.file.{OutputFile => IOOutputFile}
import pl.onewebpro.hocones.md.MdGenerator
import pl.onewebpro.hocones.md.config.Configuration.DocumentConfiguration
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

object EnvironmentDocs {

  import pl.onewebpro.hocones.cli.show.showStr

  case class EnvironmentDocsCommand(
    input: InputFile,
    output: Option[OutputFile]
  ) extends CliCommand

  object EnvironmentDocsCommand {

    def fromCommand(command: CliCommand): EnvironmentDocsCommand =
      EnvironmentDocsCommand(input = command.input, output = None)
  }

  val environmentDocsCommandOpts: Opts[EnvironmentDocsCommand] =
    (InputFile.opts, OutputFile.opts("environment documentation").orNone)
      .mapN(EnvironmentDocsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("env-docs", "generate markdown table with environments")(environmentDocsCommandOpts)

  implicit private def mapCommandToConfig: EnvironmentDocsCommand => DocumentConfiguration = { command =>
    DocumentConfiguration(
      outputPath = command.output
        .getOrElse(IOOutputFile.fromInputPath(command.input, ".md"))
        .toPath
    )
  }

  val environmentDocsCommand: Kleisli[IO, (HoconResult, MetaInformation, EnvironmentDocsCommand), Unit] = Kleisli {
    case (hocon, meta, environmentCommand) =>
      for {
        documentConfiguration <- IO[DocumentConfiguration](environmentCommand)
        _ <- putStrLn(Color.Green("Generating documentation about environments"))
        _ <- MdGenerator
          .generateDocument(hocon, meta, documentConfiguration)
          .toIO
        _ <- putStrLn(
          Color
            .Green("File generated: ") ++ documentConfiguration.outputPath.toFile.getAbsolutePath
        )
      } yield ()
  }

}
