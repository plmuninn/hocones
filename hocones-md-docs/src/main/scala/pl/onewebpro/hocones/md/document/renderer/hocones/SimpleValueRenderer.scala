package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.simple.SimpleValue

object SimpleValueRenderer {
  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[SimpleValue] = { value =>
    frag(b("Value:"), value.value)
  }
}
