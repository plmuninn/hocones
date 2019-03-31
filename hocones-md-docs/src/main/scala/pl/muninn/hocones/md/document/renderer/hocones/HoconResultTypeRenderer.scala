package pl.muninn.hocones.md.document.renderer.hocones

import cats.implicits._
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.md.document.renderer.CommonRenderingOps
import pl.muninn.hocones.parser.entity.HoconResultType

object HoconResultTypeRenderer {

  import pl.muninn.scalamdtag._
  import pl.muninn.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconResultType] = { value =>
    frag(
      List(
        frag(b("Size:"), value.size.toString, br).pure[Option],
        CommonRenderingOps
          .environmentTable(value.environments)
          .map(md => if (value.references.nonEmpty) md + br else md),
        CommonRenderingOps
          .references(value.references)
          .map(md => if (value.environments.nonEmpty) md + br else md),
        CommonRenderingOps
          .unresolvedReferences(value.unresolvedReferences),
      ).flatten
    )
  }
}
