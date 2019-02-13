package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.parser.entity.simple.ResolvedRef
import pl.onewebpro.hocones.parser.ops.DefaultValue

object ResolvedRefRenderer {

  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[ResolvedRef] = { value =>
    val referenceValueMarkdown: Markdown = NotResolvedRefRenderer.renderer.toMd(value.reference)
    val defaultValueMarkdown: Option[Markdown] =
      DefaultValue.createDefaultValue(value.result).map(default => frag(b("Reference value:"), default))

    frag(
      defaultValueMarkdown.map(_ => referenceValueMarkdown + br).getOrElse(referenceValueMarkdown),
      defaultValueMarkdown.getOrElse(CommonRenderingOps.empty)
    )

  }
}
