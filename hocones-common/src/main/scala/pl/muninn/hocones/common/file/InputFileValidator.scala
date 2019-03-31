package pl.muninn.hocones.common.file
import pl.muninn.hocones.common.file.FileErrors.{FileException, FileIsDirectory, FileIsNotReadable, FileNotExists}

object InputFileValidator {

  private lazy val unit: Unit = ()

  private[file] def validateFile(file: InputFile): Either[FileException, Unit] =
    for {
      _ <- Either.cond(file.exists, unit, FileNotExists(file))
      _ <- Either.cond(file.isFile, unit, FileIsDirectory(file))
      _ <- Either.cond(file.canRead, unit, FileIsNotReadable(file))
    } yield ()

  def validate(file: InputFile): Either[FileException, Unit] =
    validateFile(file)
}
