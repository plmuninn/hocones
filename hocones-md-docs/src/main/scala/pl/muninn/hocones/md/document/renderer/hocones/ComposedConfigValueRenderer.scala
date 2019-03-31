package pl.muninn.hocones.md.document.renderer.hocones

import cats.implicits._
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.md.document.renderer.CommonRenderingOps
import pl.muninn.hocones.parser.entity.simple.ComposedConfigValue

object ComposedConfigValueRenderer {

  import pl.muninn.scalamdtag._
  import pl.muninn.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[ComposedConfigValue] = { concatenation =>
    frag(
      List(
        frag(b("Pattern:"), concatenation.pattern, br).pure[Option],
        CommonRenderingOps
          .environmentTable(concatenation.environments)
          .map(md => if (concatenation.references.nonEmpty) md + br else md),
        CommonRenderingOps
          .references(concatenation.references)
          .map(md => if (concatenation.environments.nonEmpty) md + br else md),
        CommonRenderingOps.unresolvedReferences(concatenation.unresolvedReferences),
      ).flatten
    )
  }

}
