package pl.onewebpro.hocones.common.file
import java.io.File

object FileErrors {

  sealed trait FileException {
    def message: String
  }

  case class ParentFileNotExists[T <: File](directory: T) extends FileException {
    override def message: String =
      s"Directory ${directory.getAbsoluteFile} not exists"
  }

  case class ParentFileIsNotDirectory[T <: File](directory: T) extends FileException {
    override def message: String =
      s"File ${directory.getAbsolutePath} is not directory"
  }

  case class ParentFileIsNotWritable[T <: File](directory: T) extends FileException {
    override def message: String =
      s"Directory ${directory.getAbsolutePath} is not writable"
  }

  case class FileIsDirectory[T <: File](file: T) extends FileException {
    override def message: String =
      s"Expecting ${file.getAbsolutePath} to be file not directory"
  }

  case class FileIsNotWritable[T <: File](file: T) extends FileException {
    override def message: String =
      s"File ${file.getAbsoluteFile} is not writable"
  }

  case class FileIsNotReadable[T <: File](file: T) extends FileException {
    override def message: String =
      s"File ${file.getAbsoluteFile} is not readable"
  }

  case class FileNotExists[T <: File](file: T) extends FileException {
    override def message: String =
      s"File ${file.getAbsoluteFile} not exists"
  }
}
