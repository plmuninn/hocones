package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ObjectDocument

object ObjectDocumentRenderer {

  import pl.muninn.scalamdtag._

  //TODO
  lazy val renderer: DocumentToMarkdown[ObjectDocument] = { document =>
    frag()
  }
}
