package pl.onewebpro.hocones.md.document.renderer

import pl.onewebpro.hocones.md.document.DocumentToMarkdown
import pl.onewebpro.hocones.meta.document.model.ArrayDocument

object ArrayDocumentRenderer extends CommonRenderingOps {

  import pl.muninn.scalamdtag._

  lazy val renderer: DocumentToMarkdown[ArrayDocument] = { document =>
    template(document) {
      frag(
        frag(b("Size:"), document.size.toString) + br,
        CommonRenderingOps
          .environmentTable(document.environments)
          .map(_ + br)
          .getOrElse(CommonRenderingOps.empty),
        CommonRenderingOps
          .references(document.references)
          .map(md => if (document.environments.nonEmpty) md + br else md)
          .getOrElse(CommonRenderingOps.empty),
        CommonRenderingOps
          .unresolvedReferences(document.unresolvedReferences)
          .map(md => if (document.environments.nonEmpty || document.references.nonEmpty) md + br else md)
          .getOrElse(CommonRenderingOps.empty),
      )
    }
  }
}
