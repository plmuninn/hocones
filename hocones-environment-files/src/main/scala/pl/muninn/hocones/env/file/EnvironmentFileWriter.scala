package pl.muninn.hocones.env.file

import java.io.PrintWriter

import cats.effect.{Resource, SyncIO}
import cats.implicits._
import pl.muninn.hocones.common.file._
import pl.muninn.hocones.env.model.EnvironmentValue

class EnvironmentFileWriter(file: OutputFile) {

  import pl.muninn.hocones.env.model.show._

  private[file] def composeEnvironmentValues(values: Iterable[EnvironmentValue]): SyncIO[Iterable[String]] =
    SyncIO(values.map(_.show + "\n"))

  private[file] def writeValuesToFile(values: Iterable[String]): SyncIO[OutputFile] =
    Resource
      .fromAutoCloseable(SyncIO(new PrintWriter(file)))
      .use(printer => SyncIO(values.foreach(printer.println))) *> SyncIO.pure(file)

  def write(values: Iterable[EnvironmentValue]): SyncIO[OutputFile] =
    for {
      composedValues <- composeEnvironmentValues(values)
      output <- writeValuesToFile(composedValues)
    } yield output
}
