package pl.onewebpro.hocones.md.table

import java.util

import cats.effect.SyncIO
import net.steppschuh.markdowngenerator.table.{Table, TableRow}
import pl.onewebpro.hocones.md.config.Configuration.{TableAlignment, TableConfiguration}
import pl.onewebpro.hocones.md.table.model.EnvironmentTableElement

class EnvironmentTable(configuration: TableConfiguration) {

  private[table] lazy val alignment = configuration.aligned match {
    case TableAlignment.Center => Table.ALIGN_CENTER
    case TableAlignment.Left => Table.ALIGN_LEFT
    case TableAlignment.Right => Table.ALIGN_RIGHT
  }

  private[table] val columns = List("Environment", "Description", "Default", "Details", "Optional", "Path")

  private[table] def mapDetails(details: Map[String, String]): String =
    details.toList.map { case (key, value) => s"$key:$value" }.mkString(";")

  private[table] def mapElementToRow(element: EnvironmentTableElement): TableRow[String] =
    new TableRow[String](util.Arrays.asList(
      element.environmentVariable,
      element.description.getOrElse(""),
      element.defaultValue.getOrElse(""),
      mapDetails(element.details),
      element.isOptional.toString,
      element.path
    ))

  def fromRows(elements: Seq[EnvironmentTableElement]): SyncIO[String] = for {
    builder <- SyncIO(new Table.Builder())
    builder <- SyncIO(builder.withAlignment(alignment))
    builder <- SyncIO(builder.addRow(columns: _*))
    columns <- SyncIO(elements.map(mapElementToRow))
    builder <- SyncIO(columns.foldLeft(builder) { (table, row) => table.addRow(row) })
    table <- SyncIO(builder.build())
  } yield table.toString

}
