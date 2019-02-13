package pl.onewebpro.hocones.md.document.renderer.document

import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.meta.document.model.{DocumentType, MergedValuesDocument}
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple._

object MergedValuesDocumentRenderer extends CommonRenderingOps {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.md.document.renderer.hocones.ResultRenderer.{renderer => hoconResultRenderer}

  private def valueType(result: Result): Markdown = {
    val typeName: String = result match {
      case _: ComposedConfigValue => DocumentType.ConcatenationDocument.toString
      case _: HoconConcatenation  => DocumentType.ConcatenationDocument.toString

      case _: EnvironmentValue      => DocumentType.EnvironmentDocument.toString
      case _: HoconEnvironmentValue => DocumentType.EnvironmentDocument.toString

      case _: NotResolvedRef      => DocumentType.ReferenceValueDocument.toString
      case _: HoconReferenceValue => DocumentType.ReferenceValueDocument.toString

      case _: ResolvedRef            => DocumentType.ResolvedReferenceDocument.toString
      case _: HoconResolvedReference => DocumentType.ResolvedReferenceDocument.toString

      case _: SimpleValue => DocumentType.ValueDocument.toString
      case _: HoconValue  => DocumentType.ValueDocument.toString

      case _: HoconArray        => DocumentType.ArrayDocument.toString
      case _: HoconMergedValues => DocumentType.MergedValuesDocument.toString
      case _: HoconObject       => DocumentType.ObjectDocument.toString
    }

    frag(b("Value type:"), typeName, br)
  }

  lazy val renderer: ToMarkdown[MergedValuesDocument] = { document =>
    template(document) {
      frag(
        h3("Default value"),
        valueType(document.value.defaultValue),
        hoconResultRenderer.toMd(document.value.defaultValue),
        h3("Replacing value"),
        valueType(document.value.replacedValue),
        hoconResultRenderer.toMd(document.value.replacedValue),
      )
    }
  }
}
