package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ConcatenationDocument

object ConcatenationDocumentRenderer {

  import pl.muninn.scalamdtag._

  //TODO
  lazy val renderer: DocumentToMarkdown[ConcatenationDocument] = { document =>
    frag()
  }
}
