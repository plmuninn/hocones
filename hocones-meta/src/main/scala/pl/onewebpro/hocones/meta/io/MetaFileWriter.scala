package pl.onewebpro.hocones.meta.io

import java.io.{File, PrintWriter}

import cats.effect.{Resource, SyncIO}
import cats.implicits._
import io.circe.Json
import io.circe.yaml.Printer
import shapeless.tag
import shapeless.tag.@@

object MetaFileWriter {

  def fileName(file: File): String = file.getAbsolutePath + ".hmeta"

  private[MetaFileWriter] object MetaFileWriterInternal {
    type MetaFileTag
  }

  import MetaFileWriterInternal._

  type MetaFile = File @@ MetaFileTag

  def tagMetaFile(path: File): MetaFile = tag[MetaFileTag][File](path)

  def printer: SyncIO[Printer] =
    SyncIO(Printer(dropNullKeys = false, preserveOrder = true, mappingStyle = Printer.FlowStyle.Block, indent = 2))

  def metaFilePointer(input: File): SyncIO[MetaFile] = for {
    name <- SyncIO(fileName(input))
    metaFile <- SyncIO(tagMetaFile(new File(name)))
  } yield metaFile

  private[io] def createIfNotExists(file: MetaFile): SyncIO[Unit] =
    if (!file.exists()) SyncIO(file.createNewFile()) *> SyncIO.unit else SyncIO.unit

  def printToFile(file: MetaFile, json: Json): SyncIO[Unit] =
    printer
      .map(_.pretty(json))
      .map(_.lines.map(_.replaceAll(": null$", ": ")).mkString("\n"))
      .flatMap { text =>
        Resource.fromAutoCloseable(SyncIO(new PrintWriter(file)))
          .use(printer => SyncIO(printer.print(text)))
      }

  def create(input: File): SyncIO[MetaFile] = for {
    metaFile <- metaFilePointer(input)
    _ <- createIfNotExists(metaFile)
  } yield metaFile

}
