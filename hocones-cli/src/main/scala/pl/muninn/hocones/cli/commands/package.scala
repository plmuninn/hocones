package pl.muninn.hocones.cli

import pl.muninn.hocones.cli.arguments.InputFile.InputFile

package object commands {

  trait CliCommand {
    def input: InputFile
  }
}
