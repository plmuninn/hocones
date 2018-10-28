package pl.onewebpro.hocones.env

import java.io.File

import shapeless.tag
import shapeless.tag.@@

package object io {

  private[io] object InternalEnvironmentFileWriter {

    trait OutputFileTag

    trait ParentDirectoryTag

  }

  case class EnvironmentFileError(message: String, cause: Throwable = None.orNull) extends Error

  import InternalEnvironmentFileWriter._

  type OutputFile = File @@ OutputFileTag

  type ParentDirectory = File @@ ParentDirectoryTag

  def tagOutputFile(file: File): OutputFile = tag[OutputFileTag][File](file)

  def tagParentDirectory(file: File): ParentDirectory = tag[ParentDirectoryTag][File](file)
}
