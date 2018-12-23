package pl.onewebpro.hocones.cli.commands
import java.io.File

import com.monovore.decline.Opts
import pl.onewebpro.hocones.cli.arguments.InputFile

object Statistics {

  case class StatisticsCommand(input: File) extends CliCommand

  private val commandF: Opts[CliCommand] = InputFile.opts.map(StatisticsCommand.apply)

  val cmd: Opts[CliCommand] =
    Opts.subcommand("statistics", "display statistics about configuration")(commandF)
}
