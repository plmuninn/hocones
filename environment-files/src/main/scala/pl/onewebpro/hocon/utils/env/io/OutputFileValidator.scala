package pl.onewebpro.hocon.utils.env.io

import cats.data.Validated
import cats.implicits._

object OutputFileValidator {

  private lazy val unit: Unit = ()

  private[io] def validateParent(directory: ParentDirectory): Validation = for {
    _ <- if (directory.exists()) unit.valid else ParentFileNotExists(directory).invalid
    _ <- if (directory.isDirectory) unit.valid else ParentFileIsNotDirectory(directory).invalid
    _ <- if (directory.canWrite) unit.valid else ParentFileIsNotWritable(directory).invalid
  } yield ()

  private[io] def validateFile(file: OutputFile): Validation =
    if (!file.exists()) unit.valid else for {
      _ <- if (file.isDirectory) unit.valid else FileIsDirectory(file).invalid
      _ <- if (file.canWrite) unit.valid else FileIsNotWritable(file).invalid
    } yield ()

  def validate(file: OutputFile, directory: ParentDirectory): Validation = for {
    _ <- validateParent(directory)
    _ <- validateFile(file)
  } yield ()


  private type Validation = Validated[EnvironmentFileException, Unit]

  sealed trait EnvironmentFileException {
    def message: String
  }

  case class ParentFileNotExists(directory: ParentDirectory) extends EnvironmentFileException {
    override def message: String = s"Directory ${directory.getAbsoluteFile} not exists"
  }

  case class ParentFileIsNotDirectory(directory: ParentDirectory) extends EnvironmentFileException {
    override def message: String = s"File ${directory.getAbsolutePath} is not directory"
  }

  case class ParentFileIsNotWritable(directory: ParentDirectory) extends EnvironmentFileException {
    override def message: String = s"Directory ${directory.getAbsolutePath} is not writable"
  }

  case class FileIsDirectory(file: OutputFile) extends EnvironmentFileException {
    override def message: String = s"Expecting ${file.getAbsolutePath} to be file not directory"
  }

  case class FileIsNotWritable(file: OutputFile) extends EnvironmentFileException {
    override def message: String = s"File ${file.getAbsoluteFile} is not writable"
  }

}
