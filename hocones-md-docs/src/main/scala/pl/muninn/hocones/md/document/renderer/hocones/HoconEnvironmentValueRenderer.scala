package pl.muninn.hocones.md.document.renderer.hocones
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity.HoconEnvironmentValue

object HoconEnvironmentValueRenderer {

  lazy val renderer: ToMarkdown[HoconEnvironmentValue] = {
    case HoconEnvironmentValue(_, _, value) => EnvironmentValueRenderer.renderer.toMd(value)
  }
}
