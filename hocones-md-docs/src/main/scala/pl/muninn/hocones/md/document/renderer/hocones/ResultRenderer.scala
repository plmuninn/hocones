package pl.muninn.hocones.md.document.renderer.hocones

import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity._
import pl.muninn.hocones.parser.entity.simple._

object ResultRenderer {

  lazy val renderer: ToMarkdown[Result] = {
    case value: HoconValue  => HoconValueRenderer.renderer.toMd(value)
    case value: SimpleValue => SimpleValueRenderer.renderer.toMd(value)

    case value: HoconResultType => HoconResultTypeRenderer.renderer.toMd(value)

    case value: HoconResolvedReference => HoconResolvedReferenceRenderer.renderer.toMd(value)
    case value: HoconReferenceValue    => HoconReferenceValueRenderer.renderer.toMd(value)

    case value: NotResolvedRef => NotResolvedRefRenderer.renderer.toMd(value)
    case value: ResolvedRef    => ResolvedRefRenderer.renderer.toMd(value)

    case value: HoconEnvironmentValue => HoconEnvironmentValueRenderer.renderer.toMd(value)
    case value: EnvironmentValue      => EnvironmentValueRenderer.renderer.toMd(value)

    case value: HoconConcatenation  => HoconConcatenationRenderer.renderer.toMd(value)
    case value: ComposedConfigValue => ComposedConfigValueRenderer.renderer.toMd(value)
  }
}
