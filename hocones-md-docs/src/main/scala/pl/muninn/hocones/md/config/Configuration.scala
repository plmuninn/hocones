package pl.muninn.hocones.md.config

import java.nio.file.Path

import pl.muninn.hocones.md.config.Configuration.TableAlignment.TableAlignment

object Configuration {

  object TableAlignment extends Enumeration {
    type TableAlignment = Value

    val Left, Right, Center: TableAlignment = Value
  }

  case class TableConfiguration(outputPath: Path, aligned: TableAlignment)

  case class DocumentConfiguration(outputPath: Path)

}
