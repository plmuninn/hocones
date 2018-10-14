package pl.onewebpro.hocon.utils.env.io

import java.io.PrintWriter

import cats.effect.IO
import cats.implicits._
import pl.onewebpro.hocon.utils.env.model.EnvironmentValue

class EnvironmentFileWriter(file:OutputFile) {

  private[io] def environmentValueToString(value: EnvironmentValue): Iterable[String] =
    Iterable(value.comment.getOrElse(""), s"${value.name}=${value.defaultValue.getOrElse("")}")

  private[io] def composeEnvironmentValues(values: Iterable[EnvironmentValue]): IO[Iterable[String]] =
    IO(values.map(environmentValueToString).filter(_.nonEmpty).flatten)

  private[io] def writeValuesToFile(values: Iterable[String]): IO[OutputFile] = for {
    printer <- IO(new PrintWriter(file))
    _ <- IO(values.foreach(printer.println))
  } yield IO.pure(file)

  def write(values: Iterable[EnvironmentValue]): IO[OutputFile] =
    for {
      composedValues <- composeEnvironmentValues(values)
      output <- writeValuesToFile(composedValues)
    } yield output
}
