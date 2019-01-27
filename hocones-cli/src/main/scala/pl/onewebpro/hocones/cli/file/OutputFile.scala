package pl.onewebpro.hocones.cli.file
import java.io.File

import pl.onewebpro.hocones.cli.arguments.InputFile.InputFile
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
