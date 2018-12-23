package pl.onewebpro.hocones.cli.commands
import java.io.File

import com.monovore.decline.{Command, Opts}
import pl.onewebpro.hocones.cli.arguments.InputFile

object Hocones {

  case class HoconesCommand(input: File) extends CliCommand

  private val hoconesF: Opts[HoconesCommand] = InputFile.opts.map(HoconesCommand.apply)

  private val commandF: Opts[CliCommand] =
    Statistics.cmd
      .orElse(Environment.cmd)
      .orElse(EnvironmentDocs.cmd)
      .orElse(Docs.cmd)
      .orElse(hoconesF)

  val cmd: Command[CliCommand] =
    Command(
      name = "hocones",
      header = "set of utilities for hocon format configuration files"
    )(commandF)
}
