package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.parser.entity.HoconResultType

object HoconResultTypeRenderer {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconResultType] = { value =>
    frag(
      frag(b("Size:"), value.size.toString) + br,
      CommonRenderingOps
        .environmentTable(value.environments)
        .map(_ + br)
        .getOrElse(CommonRenderingOps.empty),
      CommonRenderingOps
        .references(value.references)
        .map(md => if (value.environments.nonEmpty) md + br else md)
        .getOrElse(CommonRenderingOps.empty),
      CommonRenderingOps
        .unresolvedReferences(value.unresolvedReferences)
        .map(md => if (value.environments.nonEmpty || value.references.nonEmpty) md + br else md)
        .getOrElse(CommonRenderingOps.empty),
    )
  }
}
