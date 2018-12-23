package pl.onewebpro.hocones.cli
import java.io.File

package object commands {
  trait CliCommand {
    def input: File
  }
}
