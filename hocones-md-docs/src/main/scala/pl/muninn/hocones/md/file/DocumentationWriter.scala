package pl.muninn.hocones.md.file

import java.io.PrintWriter

import cats.implicits._
import cats.effect.{Resource, SyncIO}
import pl.muninn.hocones.common.file.OutputFile

class DocumentationWriter(outputPath: OutputFile) {

  def write(docs: String): SyncIO[OutputFile] =
    Resource
      .fromAutoCloseable(SyncIO(new PrintWriter(outputPath)))
      .use(printer => SyncIO(printer.print(docs))) *> SyncIO.pure(outputPath)

}
