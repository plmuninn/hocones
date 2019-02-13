package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue

object EnvironmentValueRenderer {

  import pl.muninn.scalamdtag._

  lazy val renderer: ToMarkdown[EnvironmentValue] = { value =>
    frag(
      frag(b("Environment name:"), value.name) + br,
      frag(b("Is optional:"), value.isOptional.toString)
    )
  }
}
