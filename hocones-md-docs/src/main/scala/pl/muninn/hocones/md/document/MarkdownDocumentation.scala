package pl.muninn.hocones.md.document

import cats.effect.SyncIO
import pl.muninn.scalamdtag.tags.Markdown
import pl.muninn.hocones.meta.document.model.{Document, Documentation}

object MarkdownDocumentation {

  import pl.muninn.scalamdtag._

  def fromDocumentation(
    documentation: Documentation
  )(implicit renderer: ToMarkdown[Document]): SyncIO[Markdown] = SyncIO {
    markdown(
      h1("Documentation"),
      frag(documentation.roots.values.flatten.map(renderer.toMd)),
      frag(documentation.orphans.map(renderer.toMd))
    )
  }
}
