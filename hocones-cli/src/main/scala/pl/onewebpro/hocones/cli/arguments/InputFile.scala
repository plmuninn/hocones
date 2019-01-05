package pl.onewebpro.hocones.cli.arguments

import java.io.File
import java.nio.file.Path

import cats.data.{Validated, ValidatedNel}
import com.monovore.decline.Opts
import com.typesafe.config.ConfigFactory
import shapeless.tag
import shapeless.tag.@@

import scala.util.Try

object InputFile {

  private[InputFile] object InputFileInternal {
    trait InputFileTag
  }

  import InputFileInternal._

  type InputFile = File @@ InputFileTag

  private def tagInputFile(file: File): InputFile =
    tag[InputFileTag][File](file)

  private[arguments] def exists: File => ValidatedNel[String, File] = { file =>
    if (file.exists) Validated.valid(file)
    else Validated.invalidNel(s"File ${file.getAbsolutePath} not exists")
  }

  private[arguments] def canRead: File => ValidatedNel[String, File] = { file =>
    if (file.canRead) Validated.valid(file)
    else Validated.invalidNel(s"File ${file.getAbsolutePath} is not readable")
  }

  private[arguments] def isHocon: File => ValidatedNel[String, File] = { file =>
    if (Try(ConfigFactory.parseFile(file)).isSuccess) Validated.valid(file)
    else
      Validated.invalidNel(s"File ${file.getAbsolutePath} is not proper hocon")
  }

  val opts: Opts[InputFile] =
    Opts
      .argument[Path](metavar = "file")
      .map(_.toFile)
      .mapValidated(exists)
      .mapValidated(canRead)
      .mapValidated(isHocon)
      .map(tagInputFile)
}
