package pl.muninn.hocones.md.document.renderer.hocones
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity.HoconConcatenation

object HoconConcatenationRenderer {

  lazy val renderer: ToMarkdown[HoconConcatenation] = { concatenation =>
    ComposedConfigValueRenderer.renderer.toMd(concatenation.value)
  }
}
