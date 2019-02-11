package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ReferenceValueDocument

object ReferenceValueDocumentRenderer extends CommonRenderingOps {

  import pl.muninn.scalamdtag._

  lazy val renderer: DocumentToMarkdown[ReferenceValueDocument] = { document =>
    template(document) {
      frag(
        frag(b("Reference to:"), document.value.result.name) + br,
        frag(b("Is optional:"), document.value.result.isOptional.toString)
      )
    }
  }
}
