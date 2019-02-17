package pl.onewebpro.hocones.md.document.renderer.hocones

import cats.implicits._
import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.HoconResolvedReference

object HoconResolvedReferenceRenderer {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconResolvedReference] = { value =>
    val referenceValueMarkdown: Markdown = HoconReferenceValueRenderer.renderer.toMd(value.reference)

    val defaultValueMarkdown: Option[Markdown] =
      value.referenceValue.map(default => frag(b("Reference value:"), default))

    frag(
      List(
        defaultValueMarkdown.map(_ => referenceValueMarkdown + br).getOrElse(referenceValueMarkdown).pure[Option],
        defaultValueMarkdown
      ).flatten
    )
  }
}
