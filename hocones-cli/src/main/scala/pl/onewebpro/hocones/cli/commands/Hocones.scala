package pl.onewebpro.hocones.cli.commands
import java.io.File

import com.monovore.decline.Command
import pl.onewebpro.hocones.cli.arguments.InputFile

object Hocones {
  val cmd: Command[File] =
    Command(
      name = "hocones",
      header = "set of utilities for hocon format configuration files"
    )(InputFile.opts)
}
