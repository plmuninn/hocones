package pl.onewebpro.hocon.utils.env.io

import java.io.PrintWriter

import cats.effect.{Resource, SyncIO}
import cats.implicits._
import pl.onewebpro.hocon.utils.env.model.EnvironmentValue

class EnvironmentFileWriter(file: OutputFile) {

  private[io] def environmentValueToString(value: EnvironmentValue): Iterable[String] =
    value.comments ++ Iterable(s"${value.name}=${value.defaultValue.getOrElse("")}\n")

  private[io] def composeEnvironmentValues(values: Iterable[EnvironmentValue]): SyncIO[Iterable[String]] =
    SyncIO(values.map(environmentValueToString).filter(_.nonEmpty).flatten)

  private[io] def writeValuesToFile(values: Iterable[String]): SyncIO[OutputFile] =
    Resource.fromAutoCloseable(SyncIO(new PrintWriter(file)))
      .use(printer => SyncIO(values.foreach(printer.println))) *> SyncIO.pure(file)

  def write(values: Iterable[EnvironmentValue]): SyncIO[OutputFile] =
    for {
      composedValues <- composeEnvironmentValues(values)
      output <- writeValuesToFile(composedValues)
    } yield output
}
