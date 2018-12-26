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
import pl.onewebpro.hocones.md.config.Configuration.{TableConfiguration, TableAlignment => MdTableAlignment}
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

object Docs {

  import pl.onewebpro.hocones.cli.show.showStr

  case class DocsCommand(input: InputFile, output: Option[OutputFile], alignment: MdTableAlignment.TableAlignment)
      extends CliCommand

  object DocsCommand {
    def fromCommand(cliCommand: CliCommand): DocsCommand =
      DocsCommand(input = cliCommand.input, output = None, alignment = TableAlignment.defaultAlignment)
  }

  val docsCommandF: Opts[DocsCommand] =
    (InputFile.opts, OutputFile.opts("documentation").orNone, TableAlignment.opts).mapN(DocsCommand.apply)

  val cmd: Opts[CliCommand] = Opts.subcommand("docs", "generate md table with environments")(docsCommandF)

  implicit private def mapCommandToConfig: DocsCommand => TableConfiguration = { command =>
    TableConfiguration(
      outputPath = command.output.getOrElse(IOOutputFile.fromInputPath(command.input, ".env.md")).toPath,
      aligned = command.alignment
    )
  }

  val docsCommand: Kleisli[IO, (HoconResult, MetaInformation, DocsCommand), Unit] = Kleisli {
    case (hocon, meta, command) =>
      for {
        tableConfiguration <- IO[TableConfiguration](command)
        _ <- putStrLn(Color.Green("Generating documentation about configuration"))
        _ <- MdGenerator
          .generateTable(hocon, meta, tableConfiguration)
          .toIO
        _ <- putStrLn(Color.Green("File generated: ") ++ tableConfiguration.outputPath.toFile.getAbsolutePath)
      } yield ()
  }
}
