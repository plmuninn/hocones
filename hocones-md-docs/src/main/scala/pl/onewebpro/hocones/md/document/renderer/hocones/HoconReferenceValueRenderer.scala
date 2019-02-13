package pl.onewebpro.hocones.md.document.renderer.hocones
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.parser.entity.HoconReferenceValue

object HoconReferenceValueRenderer {

  import NotResolvedRefRenderer.{renderer => notResolvedRefRenderer}

  lazy val renderer: ToMarkdown[HoconReferenceValue] = { value =>
    notResolvedRefRenderer.toMd(value.result)
  }
}
