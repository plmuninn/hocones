package pl.onewebpro.hocones.md.table

import pl.muninn.scalamdtag.tags.Markdown

object EnvironmentTableGenerator {

  def generateTable(values: Seq[EnvironmentTableElement]): Markdown = {
    import pl.muninn.scalamdtag._

    markdown(
      h1("Configuration environments"),
      table(
        ("Environment", "Description", "Default", "Details", "Is optional", "Path"),
        values.map(EnvironmentTableElement.unapply).collect {
          case Some(tupled) => tupled
        }
      )
    )
  }

}
