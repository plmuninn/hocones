package pl.onewebpro.hocones.cli.commands
import java.io.File

import cats.implicits._
import com.monovore.decline.Opts
import pl.onewebpro.hocones.cli.arguments.docs.TableAlignment
import pl.onewebpro.hocones.cli.arguments.{InputFile, OutputFile}
import pl.onewebpro.hocones.md.config.Configuration.{TableAlignment => TA}

object EnvironmentDocs {

  case class EnvironmentDocsCommand(input: File, output: Option[File], alignment: TA.TableAlignment) extends CliCommand

  private val environmentDocsCommandF: Opts[EnvironmentDocsCommand] =
    (InputFile.opts, OutputFile.opts("environment documentation").orNone, TableAlignment.opts)
      .mapN(EnvironmentDocsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("env-docs", "generate md table with environments")(environmentDocsCommandF)

}
