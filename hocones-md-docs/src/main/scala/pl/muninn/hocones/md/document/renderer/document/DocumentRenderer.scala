package pl.muninn.hocones.md.document.renderer.document
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.md.document.renderer.CommonRenderingOps
import pl.muninn.hocones.meta.document.model._

object DocumentRenderer extends CommonRenderingOps {

  import pl.muninn.hocones.md.document.renderer.hocones.ResultRenderer.{renderer => hoconResultRenderer}

  lazy val renderer: ToMarkdown[Document] = {
    case merged: MergedValuesDocument => MergedValuesDocumentRenderer.renderer.toMd(merged)
    case document: Document           => template(document)(hoconResultRenderer.toMd(document.value))
  }
}
