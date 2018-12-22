package pl.onewebpro.hocones.md.io

import java.io.PrintWriter

import cats.implicits._
import cats.effect.{Resource, SyncIO}
import pl.onewebpro.hocones.common.io.OutputFile

class DocumentationWriter(outputPath: OutputFile) {

  def write(docs: String): SyncIO[OutputFile] =
    Resource
      .fromAutoCloseable(SyncIO(new PrintWriter(outputPath)))
      .use(printer => SyncIO(printer.print(docs))) *> SyncIO.pure(outputPath)

}
