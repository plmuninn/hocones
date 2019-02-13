package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.parser.entity.simple.ResolvedRef
import pl.onewebpro.hocones.parser.ops.DefaultValue

object ResolvedRefRenderer {

  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[ResolvedRef] = { value =>
    frag(
      NotResolvedRefRenderer.renderer.toMd(value.reference),
      DefaultValue.createDefaultValue(value.result) match {
        case Some(default) => br + frag(b("Reference value:"), default)
        case _             => CommonRenderingOps.empty
      }
    )

  }
}
