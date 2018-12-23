package pl.onewebpro.hocones.cli.commands
import java.io.File

import cats.implicits._
import com.monovore.decline.Opts
import pl.onewebpro.hocones.cli.arguments.environment.{RemoveDuplicates, WithComments, WithDefaults}
import pl.onewebpro.hocones.cli.arguments.{InputFile, OutputFile}

object Environment {

  case class EnvironmentCommand(input: File,
                                output: Option[File],
                                withComments: Boolean,
                                withDefaults: Boolean,
                                removeDuplicates: Boolean)
      extends CliCommand

  private val environmentCommandF: Opts[EnvironmentCommand] = (
    InputFile.opts,
    OutputFile.opts("environment file").orNone,
    WithComments.opts,
    WithDefaults.opts,
    RemoveDuplicates.opts
  ).mapN(EnvironmentCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand[CliCommand](name = "env-file", help = "generate environment file")(environmentCommandF)
}
