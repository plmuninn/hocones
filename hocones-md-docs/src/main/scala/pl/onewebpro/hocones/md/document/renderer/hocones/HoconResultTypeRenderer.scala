package pl.onewebpro.hocones.md.document.renderer.hocones

import cats.implicits._
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.parser.entity.HoconResultType

object HoconResultTypeRenderer {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._

  lazy val renderer: ToMarkdown[HoconResultType] = { value =>
    frag(
      List(
        frag(b("Size:"), value.size.toString, br).pure[Option],
        CommonRenderingOps
          .environmentTable(value.environments)
          .map(_ + br),
        CommonRenderingOps
          .references(value.references)
          .map(md => if (value.environments.nonEmpty) md + br else md),
        CommonRenderingOps
          .unresolvedReferences(value.unresolvedReferences),
      ).flatten
    )
  }
}
