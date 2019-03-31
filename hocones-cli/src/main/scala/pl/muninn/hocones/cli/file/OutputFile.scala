package pl.muninn.hocones.cli.file

import pl.muninn.hocones.cli.arguments.InputFile.InputFile
import java.io.File

import shapeless.tag
import shapeless.tag.@@

object OutputFile {

  private[OutputFile] object OutputFileInternal {
    trait OutputFileTag
  }

  import OutputFileInternal._

  type OutputFile = File @@ OutputFileTag

  def tagOutputFile(file: File): OutputFile = tag[OutputFileTag][File](file)

  def fromInputPath(input: InputFile, ext: String): OutputFile =
    tagOutputFile(new File(input.getAbsolutePath + ext))
}
