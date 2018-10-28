package pl.onewebpro.hocones.env.io

import cats.implicits._

object OutputFileValidator {

  private lazy val unit: Unit = ()

  private[io] def validateParent(directory: ParentDirectory): Either[EnvironmentFileException, Unit] = for {
    _ <- Either.cond(directory.exists(), unit, ParentFileNotExists(directory))
    _ <- Either.cond(directory.isDirectory, unit, ParentFileIsNotDirectory(directory))
    _ <- Either.cond(directory.canWrite, unit, ParentFileIsNotWritable(directory))
  } yield ()

  private[io] def validateFile(file: OutputFile): Either[EnvironmentFileException, Unit] =
    if (!file.exists()) Right(unit) else for {
      _ <- Either.cond(file.isFile, unit, FileIsDirectory(file))
      _ <- Either.cond(file.canWrite, unit, FileIsNotWritable(file))
    } yield ()

  def validate(file: OutputFile, directory: ParentDirectory): Either[EnvironmentFileException, Unit] =
    for {
      _ <- validateParent(directory)
      _ <- validateFile(file)
    } yield ()

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
