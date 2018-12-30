package pl.onewebpro.hocones.cli.commands
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.Opts
import fansi.Color
import pl.onewebpro.hocones.cli.arguments.InputFile.InputFile
import pl.onewebpro.hocones.cli.arguments.docs.TableAlignment
import pl.onewebpro.hocones.cli.arguments.{InputFile, OutputFile}
import pl.onewebpro.hocones.cli.io.OutputFile.OutputFile
import pl.onewebpro.hocones.cli.io.{OutputFile => IOOutputFile}
import pl.onewebpro.hocones.md.MdGenerator
import pl.onewebpro.hocones.md.config.Configuration.{
  TableConfiguration,
  TableAlignment => MdTableAlignment
}
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

object EnvironmentDocs {

  import pl.onewebpro.hocones.cli.show.showStr

  case class EnvironmentDocsCommand(input: InputFile,
                                    output: Option[OutputFile],
                                    alignment: MdTableAlignment.TableAlignment)
      extends CliCommand

  object EnvironmentDocsCommand {
    def fromCommand(command: CliCommand): EnvironmentDocsCommand =
      EnvironmentDocsCommand(input = command.input,
                             output = None,
                             alignment = TableAlignment.defaultAlignment)
  }

  private val environmentDocsCommandF: Opts[EnvironmentDocsCommand] =
    (InputFile.opts,
     OutputFile.opts("environment documentation").orNone,
     TableAlignment.opts)
      .mapN(EnvironmentDocsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("env-docs", "generate md table with environments")(
      environmentDocsCommandF)

  implicit private def mapCommandToConfig
    : EnvironmentDocsCommand => TableConfiguration = { command =>
    TableConfiguration(
      outputPath = command.output
        .getOrElse(IOOutputFile.fromInputPath(command.input, ".md"))
        .toPath,
      aligned = command.alignment
    )
  }

  val environmentDocsCommand
    : Kleisli[IO,
              (HoconResult, MetaInformation, EnvironmentDocsCommand),
              Unit] = Kleisli {
    case (hocon, meta, environmentCommand) =>
      for {
        tableConfiguration <- IO[TableConfiguration](environmentCommand)
        _ <- putStrLn(
          Color.Green("Generating documentation about environments"))
        _ <- MdGenerator
          .generateTable(hocon, meta, tableConfiguration)
          .toIO
        _ <- putStrLn(
          Color
            .Green("File generated: ") ++ tableConfiguration.outputPath.toFile.getAbsolutePath)
      } yield ()
  }

}
