package pl.onewebpro.hocones.md.document.renderer.hocones

import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.HoconValue

object HoconValueRenderer {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconValue] = { value =>
    frag(
      frag(b("Type of value:"), value.valueType.mdName) + br,
      SimpleValueRenderer.renderer.toMd(value.value)
    )
  }
}
