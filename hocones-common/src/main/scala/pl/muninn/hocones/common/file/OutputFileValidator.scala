package pl.muninn.hocones.common.file
import pl.muninn.hocones.common.file.FileErrors._

object OutputFileValidator {

  private lazy val unit: Unit = ()

  private[file] def validateParent(directory: ParentDirectory): Either[FileException, Unit] =
    for {
      _ <- Either.cond(directory.exists(), unit, ParentFileNotExists(directory))
      _ <- Either.cond(directory.isDirectory, unit, ParentFileIsNotDirectory(directory))
      _ <- Either.cond(directory.canWrite, unit, ParentFileIsNotWritable(directory))
    } yield ()

  private[file] def validateFile(file: OutputFile): Either[FileException, Unit] =
    if (!file.exists()) Right(unit)
    else
      for {
        _ <- Either.cond(file.isFile, unit, FileIsDirectory(file))
        _ <- Either.cond(file.canWrite, unit, FileIsNotWritable(file))
      } yield ()

  def validate(file: OutputFile, directory: ParentDirectory): Either[FileException, Unit] =
    for {
      _ <- validateParent(directory)
      _ <- validateFile(file)
    } yield ()

}
