package pl.onewebpro.hocones.md.document.renderer.document
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.meta.document.model._

object DocumentRenderer extends CommonRenderingOps {

  import pl.onewebpro.hocones.md.document.renderer.hocones.ResultRenderer.{renderer => hoconResultRenderer}

  lazy val renderer: ToMarkdown[Document] = {
    case merged: MergedValuesDocument => MergedValuesDocumentRenderer.renderer.toMd(merged)
    case document: Document           => template(document)(hoconResultRenderer.toMd(document.value))
  }
}
