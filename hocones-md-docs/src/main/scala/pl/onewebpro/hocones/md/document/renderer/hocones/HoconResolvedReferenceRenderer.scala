package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.parser.entity.HoconResolvedReference

object HoconResolvedReferenceRenderer {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconResolvedReference] = { value =>
    frag(
      HoconReferenceValueRenderer.renderer.toMd(value.reference),
      value.referenceValue match {
        case Some(default) => br + frag(b("Reference value:"), default)
        case _             => CommonRenderingOps.empty
      }
    )
  }
}
