package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ArrayDocument

object ArrayDocumentRenderer {

  import pl.muninn.scalamdtag._

  //TODO
  lazy val renderer: DocumentToMarkdown[ArrayDocument] = { document =>
    frag()
  }
}
