package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.parser.entity.simple.ComposedConfigValue

object ComposedConfigValueRenderer {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[ComposedConfigValue] = { concatenation =>
    frag(
      frag(b("Pattern:"), concatenation.pattern, br),
      CommonRenderingOps
        .environmentTable(concatenation.environments)
        .map(_ + br)
        .getOrElse(CommonRenderingOps.empty),
      CommonRenderingOps
        .references(concatenation.references)
        .map(md => if (concatenation.environments.nonEmpty) md + br else md)
        .getOrElse(CommonRenderingOps.empty),
      CommonRenderingOps
        .unresolvedReferences(concatenation.unresolvedReferences)
        .map(md => if (concatenation.environments.nonEmpty || concatenation.references.nonEmpty) md + br else md)
        .getOrElse(CommonRenderingOps.empty),
    )
  }

}
