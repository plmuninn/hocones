package pl.muninn.hocones.cli.commands
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.Opts
import fansi.Color
import pl.muninn.hocones.cli.arguments.InputFile.InputFile
import pl.muninn.hocones.cli.arguments.docs.TableAlignment
import pl.muninn.hocones.cli.arguments.{InputFile, OutputFile}
import pl.muninn.hocones.cli.commands.Hocones.HoconesCommand
import pl.muninn.hocones.cli.file.OutputFile.OutputFile
import pl.muninn.hocones.cli.file.{OutputFile => IOOutputFile}
import pl.muninn.hocones.md.MdGenerator
import pl.muninn.hocones.md.config.Configuration.{TableConfiguration, TableAlignment => MdTableAlignment}
import pl.muninn.hocones.meta.document.GenerateDocumentation
import pl.muninn.hocones.meta.model.MetaInformation
import pl.muninn.hocones.parser.HoconResult

object EnvironmentDocs {

  import pl.muninn.hocones.cli.show.showStr

  case class EnvironmentDocsCommand(input: InputFile, output: Option[OutputFile], alignment: MdTableAlignment.TableAlignment)
      extends CliCommand

  object EnvironmentDocsCommand {

    def fromCommand(cliCommand: CliCommand): EnvironmentDocsCommand =
      cliCommand match {
        case HoconesCommand(input, alignment, _, _, _) =>
          EnvironmentDocsCommand(input = input, output = None, alignment = alignment.getOrElse(TableAlignment.defaultAlignment))
        case _ =>
          EnvironmentDocsCommand(input = cliCommand.input, output = None, alignment = TableAlignment.defaultAlignment)
      }
  }

  val docsCommandOpts: Opts[EnvironmentDocsCommand] =
    (InputFile.opts, OutputFile.opts("environment documentation").orNone, TableAlignment.opts).mapN(EnvironmentDocsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("env-docs", "generate markdown table with environments documentation")(docsCommandOpts)

  implicit private def mapCommandToConfig: EnvironmentDocsCommand => TableConfiguration = { command =>
    TableConfiguration(
      outputPath = command.output
        .getOrElse(IOOutputFile.fromInputPath(command.input, ".env.md"))
        .toPath,
      aligned = command.alignment
    )
  }

  val environmentDocsCommand: Kleisli[IO, (HoconResult, MetaInformation, EnvironmentDocsCommand), Unit] = Kleisli {
    case (hocon, meta, command) =>
      for {
        tableConfiguration <- IO[TableConfiguration](command)
        _ <- putStrLn(Color.Green("Generating documentation about configuration"))
        documentation <- GenerateDocumentation.generate(hocon, meta).toIO
        _ <- MdGenerator
          .generateTable(hocon, meta, documentation, tableConfiguration)
          .toIO
        _ <- putStrLn(
          Color
            .Green("File generated: ") ++ tableConfiguration.outputPath.toFile.getAbsolutePath
        )
      } yield ()
  }
}
