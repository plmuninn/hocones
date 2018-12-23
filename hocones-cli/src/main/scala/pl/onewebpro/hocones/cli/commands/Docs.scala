package pl.onewebpro.hocones.cli.commands
import java.io.File

import cats.implicits._
import com.monovore.decline.Opts
import pl.onewebpro.hocones.cli.arguments.{InputFile, OutputFile}

object Docs {

  case class DocsCommand(input: File, output: Option[File]) extends CliCommand

  val docsCommandF: Opts[DocsCommand] =
    (InputFile.opts, OutputFile.opts("documentation").orNone).mapN(DocsCommand.apply)

  val cmd: Opts[CliCommand] = Opts.subcommand("docs", "generate md table with environments")(docsCommandF)

}
