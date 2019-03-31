package pl.muninn.hocones.md.document.renderer.hocones

import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity.HoconValue

object HoconValueRenderer {

  import pl.muninn.scalamdtag._
  import pl.muninn.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconValue] = { value =>
    frag(
      frag(b("Type of value:"), value.valueType.mdName) + br,
      SimpleValueRenderer.renderer.toMd(value.value)
    )
  }
}
