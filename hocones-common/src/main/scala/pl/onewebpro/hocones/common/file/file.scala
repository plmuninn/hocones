package pl.onewebpro.hocones.common

import java.io.File

import shapeless.tag.@@
import shapeless.tag

package object file {

  private[file] object InternalEnvironmentFileWriter {

    trait OutputFileTag

    trait ParentDirectoryTag

    trait InputFileTag

  }

  import InternalEnvironmentFileWriter._

  type OutputFile = File @@ OutputFileTag

  type ParentDirectory = File @@ ParentDirectoryTag

  type InputFile = File @@ InputFileTag

  def tagOutputFile(file: File): OutputFile = tag[OutputFileTag][File](file)

  def tagParentDirectory(file: File): ParentDirectory =
    tag[ParentDirectoryTag][File](file)

  def tagInputFile(file: File): InputFile = tag[InputFileTag][File](file)
}
