package pl.muninn.hocones.cli.commands
import cats.data.Kleisli
import cats.effect.Console.io.putStrLn
import cats.effect.IO
import cats.implicits._
import com.monovore.decline.Opts
import fansi.Color
import pl.muninn.hocones.cli.arguments.InputFile.InputFile
import pl.muninn.hocones.cli.arguments.{InputFile, OutputFile}
import pl.muninn.hocones.cli.file.OutputFile.OutputFile
import pl.muninn.hocones.cli.file.{OutputFile => IOOutputFile}
import pl.muninn.hocones.md.MdGenerator
import pl.muninn.hocones.md.config.Configuration.DocumentConfiguration
import pl.muninn.hocones.meta.document.GenerateDocumentation
import pl.muninn.hocones.meta.model.MetaInformation
import pl.muninn.hocones.parser.HoconResult

object Docs {

  import pl.muninn.hocones.cli.show.showStr

  case class DocsCommand(
    input: InputFile,
    output: Option[OutputFile]
  ) extends CliCommand

  object DocsCommand {

    def fromCommand(command: CliCommand): DocsCommand =
      DocsCommand(input = command.input, output = None)
  }

  val environmentDocsCommandOpts: Opts[DocsCommand] =
    (InputFile.opts, OutputFile.opts("documentation").orNone)
      .mapN(DocsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("docs", "generate markdown with documentation")(environmentDocsCommandOpts)

  implicit private def mapCommandToConfig: DocsCommand => DocumentConfiguration = { command =>
    DocumentConfiguration(
      outputPath = command.output
        .getOrElse(IOOutputFile.fromInputPath(command.input, ".md"))
        .toPath
    )
  }

  val environmentDocsCommand: Kleisli[IO, (HoconResult, MetaInformation, DocsCommand), Unit] = Kleisli {
    case (hocon, meta, environmentCommand) =>
      for {
        documentConfiguration <- IO[DocumentConfiguration](environmentCommand)
        _ <- putStrLn(Color.Green("Generating documentation about environments"))
        documentation <- GenerateDocumentation.generate(hocon, meta).toIO
        _ <- MdGenerator
          .generateDocument(hocon, documentation, documentConfiguration)
          .toIO
        _ <- putStrLn(
          Color
            .Green("File generated: ") ++ documentConfiguration.outputPath.toFile.getAbsolutePath
        )
      } yield ()
  }

}
