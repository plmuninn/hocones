package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ResolvedReferenceDocument

object ResolvedReferenceDocumentRenderer extends CommonRenderingOps {

  import pl.muninn.scalamdtag._

  lazy val renderer: DocumentToMarkdown[ResolvedReferenceDocument] = { document =>
    template(document) {
      frag(b("Reference to:"), document.value.reference.path)
    }
  }
}
