package pl.onewebpro.hocones.cli
import pl.onewebpro.hocones.cli.arguments.InputFile.InputFile

package object commands {
  trait CliCommand {
    def input: InputFile
  }
}
