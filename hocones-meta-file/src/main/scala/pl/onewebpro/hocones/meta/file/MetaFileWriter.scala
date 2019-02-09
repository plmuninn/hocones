package pl.onewebpro.hocones.meta.file

import java.io.{File, PrintWriter}

import cats.effect.{Resource, SyncIO}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
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

  def metaFilePointer(input: File): SyncIO[MetaFile] =
    for {
      name <- SyncIO(fileName(input))
      metaFile <- SyncIO(tagMetaFile(new File(name)))
    } yield metaFile

  private[file] def createIfNotExists(file: MetaFile): SyncIO[Unit] =
    if (!file.exists()) for {
      logger <- Slf4jLogger.create[SyncIO]
      _ <- SyncIO(file.createNewFile())
      _ <- logger.debug(s"Meta file ${file.getAbsolutePath} created")
    } yield ()
    else SyncIO.unit

  def printToFile(file: MetaFile, json: Json): SyncIO[Unit] =
    printer
      .map(_.pretty(json))
      .map(_.lines.toList.map(_.replaceAll(": null$", ": ")).mkString("\n"))
      .flatMap { text =>
        Resource
          .fromAutoCloseable(SyncIO(new PrintWriter(file)))
          .use(printer => SyncIO(printer.print(text)))
      }

  def create(input: File): SyncIO[MetaFile] =
    for {
      metaFile <- metaFilePointer(input)
      _ <- createIfNotExists(metaFile)
    } yield metaFile

}
