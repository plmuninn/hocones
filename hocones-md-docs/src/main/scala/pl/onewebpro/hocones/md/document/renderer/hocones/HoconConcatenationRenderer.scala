package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.HoconConcatenation

object HoconConcatenationRenderer {

  lazy val renderer: ToMarkdown[HoconConcatenation] = { concatenation =>
    ComposedConfigValueRenderer.renderer.toMd(concatenation.value)
  }
}
