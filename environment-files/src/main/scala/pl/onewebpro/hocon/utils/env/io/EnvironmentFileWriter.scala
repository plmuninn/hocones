package pl.onewebpro.hocon.utils.env.io

import java.io.PrintWriter

import cats.effect.IO
import pl.onewebpro.hocon.utils.env.model.EnvironmentValue

class EnvironmentFileWriter(file: OutputFile) {

  private[io] def environmentValueToString(value: EnvironmentValue): Iterable[String] =
    value.comments ++ Iterable(s"${value.name}=${value.defaultValue.getOrElse("")}\n")

  private[io] def composeEnvironmentValues(values: Iterable[EnvironmentValue]): IO[Iterable[String]] =
    IO(values.map(environmentValueToString).filter(_.nonEmpty).flatten)

  private[io] def writeValuesToFile(values: Iterable[String]): IO[OutputFile] = for {
    printer <- IO(new PrintWriter(file))
    _ <- IO(values.foreach(printer.println))
    _ <- IO(printer.close())
    result <- IO.pure(file)
  } yield result

  def write(values: Iterable[EnvironmentValue]): IO[OutputFile] =
    for {
      composedValues <- composeEnvironmentValues(values)
      output <- writeValuesToFile(composedValues)
    } yield output
}
