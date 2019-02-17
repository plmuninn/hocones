package pl.onewebpro.hocones.cli.arguments

import java.io.File
import java.nio.file.Path

import cats.data.{Validated, ValidatedNel}
import com.monovore.decline.Opts
import pl.onewebpro.hocones.cli.file.OutputFile.{tagOutputFile, OutputFile}

object OutputFile {

  private[arguments] def canWrite: File => ValidatedNel[String, File] = { file =>
    val parentFile = file.getParentFile

    if ((file.exists() && file.canWrite) || (parentFile
          .exists() && parentFile.canWrite)) Validated.valid(file)
    else
      Validated.invalidNel(s"Output path ${file.getAbsolutePath} is unavailable")
  }

  def opts(reason: String): Opts[OutputFile] =
    Opts
      .option[Path](
        long = "output",
        help = s"output is a required file property - for saving $reason",
        short = "o",
        metavar = "file"
      )
      .map(_.toFile)
      .mapValidated(canWrite)
      .map(tagOutputFile)
}
