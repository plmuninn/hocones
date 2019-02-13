package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.simple.NotResolvedRef

object NotResolvedRefRenderer {

  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[NotResolvedRef] = { value =>
    frag(
      frag(b("Reference to:"), value.name) + br,
      frag(b("Is optional:"), value.isOptional.toString)
    )
  }
}
