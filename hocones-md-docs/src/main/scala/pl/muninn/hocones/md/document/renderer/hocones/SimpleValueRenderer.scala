package pl.muninn.hocones.md.document.renderer.hocones
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity.simple.SimpleValue

object SimpleValueRenderer {
  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[SimpleValue] = { value =>
    frag(b("Value:"), value.value)
  }
}
