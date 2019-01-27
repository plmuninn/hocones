package pl.onewebpro.hocones.common.file
import java.io.File

import pl.onewebpro.hocones.common.file.InternalEnvironmentFileWriter.{InputFileTag, OutputFileTag, ParentDirectoryTag}
import shapeless.tag

object HoconFileUtils {
  def tagOutputFile(file: File): OutputFile = tag[OutputFileTag][File](file)

  def tagParentDirectory(file: File): ParentDirectory =
    tag[ParentDirectoryTag][File](file)

  def tagInputFile(file: File): InputFile = tag[InputFileTag][File](file)
}
