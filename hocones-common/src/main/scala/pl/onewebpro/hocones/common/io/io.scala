package pl.onewebpro.hocones.common

import java.io.File

import shapeless.tag.@@
import shapeless.tag

package object io {

  private[io] object InternalEnvironmentFileWriter {

    trait OutputFileTag

    trait ParentDirectoryTag

  }

  import InternalEnvironmentFileWriter._

  type OutputFile = File @@ OutputFileTag

  type ParentDirectory = File @@ ParentDirectoryTag

  def tagOutputFile(file: File): OutputFile = tag[OutputFileTag][File](file)

  def tagParentDirectory(file: File): ParentDirectory = tag[ParentDirectoryTag][File](file)
}
