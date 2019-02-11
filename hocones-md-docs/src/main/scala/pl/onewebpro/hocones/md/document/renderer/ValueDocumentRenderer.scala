package pl.onewebpro.hocones.md.document.renderer
import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ValueDocument

object ValueDocumentRenderer extends CommonRenderingOps {

  import pl.muninn.scalamdtag._

  lazy val renderer: DocumentToMarkdown[ValueDocument] = { document =>
    template(document) {
      frag(
        frag(b("Value is quoted:"), document.quoted.toString) + br,
        frag(b("Type of value:"), document.valueType)
      )
    }
  }
}
