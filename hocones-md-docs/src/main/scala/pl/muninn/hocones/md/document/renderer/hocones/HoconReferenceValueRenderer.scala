package pl.muninn.hocones.md.document.renderer.hocones
import pl.muninn.hocones.md.document.ToMarkdown
import pl.muninn.hocones.parser.entity.HoconReferenceValue

object HoconReferenceValueRenderer {

  import NotResolvedRefRenderer.{renderer => notResolvedRefRenderer}

  lazy val renderer: ToMarkdown[HoconReferenceValue] = { value =>
    notResolvedRefRenderer.toMd(value.result)
  }
}
