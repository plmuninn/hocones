package pl.muninn.hocones.md.document.renderer.hocones

import cats.implicits._
import pl.muninn.scalamdtag.tags.Markdown
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity.simple.ResolvedRef
import pl.muninn.hocones.parser.ops.DefaultValue

object ResolvedRefRenderer {

  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[ResolvedRef] = { value =>
    val referenceValueMarkdown: Markdown = NotResolvedRefRenderer.renderer.toMd(value.reference)
    val defaultValueMarkdown: Option[Markdown] =
      DefaultValue.createDefaultValue(value.result).map(default => frag(b("Reference value:"), default))

    frag(
      List(
        defaultValueMarkdown.map(_ => referenceValueMarkdown + br).getOrElse(referenceValueMarkdown).pure[Option],
        defaultValueMarkdown
      ).flatten
    )
  }
}
