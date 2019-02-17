package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.HoconEnvironmentValue

object HoconEnvironmentValueRenderer {

  lazy val renderer: ToMarkdown[HoconEnvironmentValue] = {
    case HoconEnvironmentValue(_, _, value) => EnvironmentValueRenderer.renderer.toMd(value)
  }
}
