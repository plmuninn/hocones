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

object Docs {

  import pl.muninn.hocones.cli.show.showStr

  case class DocsCommand(input: InputFile, output: Option[OutputFile], alignment: MdTableAlignment.TableAlignment)
      extends CliCommand

  object DocsCommand {

    def fromCommand(cliCommand: CliCommand): DocsCommand =
      cliCommand match {
        case HoconesCommand(input, alignment, _, _, _) =>
          DocsCommand(input = input, output = None, alignment = alignment.getOrElse(TableAlignment.defaultAlignment))
        case _ =>
          DocsCommand(input = cliCommand.input, output = None, alignment = TableAlignment.defaultAlignment)
      }
  }

  val docsCommandOpts: Opts[DocsCommand] =
    (InputFile.opts, OutputFile.opts("documentation").orNone, TableAlignment.opts).mapN(DocsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("docs", "generate markdown table with environments")(docsCommandOpts)

  implicit private def mapCommandToConfig: DocsCommand => TableConfiguration = { command =>
    TableConfiguration(
      outputPath = command.output
        .getOrElse(IOOutputFile.fromInputPath(command.input, ".env.md"))
        .toPath,
      aligned = command.alignment
    )
  }

  val docsCommand: Kleisli[IO, (HoconResult, MetaInformation, DocsCommand), Unit] = Kleisli {
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
