package pl.onewebpro.hocones.md.document.renderer.document

import pl.muninn.scalamdtag.tags.Markdown
import pl.onewebpro.hocones.md.document.ToMarkdown
import pl.onewebpro.hocones.md.document.renderer.CommonRenderingOps
import pl.onewebpro.hocones.meta.document.model.MergedValuesDocument
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple._

object MergedValuesDocumentRenderer extends CommonRenderingOps {

  import pl.muninn.scalamdtag._
  import pl.onewebpro.hocones.meta.document.HoconValuesOps._
  import pl.onewebpro.hocones.md.document.renderer.hocones.ResultRenderer.{renderer => hoconResultRenderer}

  private def valueType(result: Result): Markdown = {
    val typeName: String = result match {
      case _: ComposedConfigValue => "Concatenation"
      case _: HoconConcatenation  => "Concatenation"

      case _: EnvironmentValue      => "Environment value"
      case _: HoconEnvironmentValue => "Environment value"

      case _: NotResolvedRef      => "Unresolved reference"
      case _: HoconReferenceValue => "Unresolved reference"

      case _: ResolvedRef            => "Resolved reference"
      case _: HoconResolvedReference => "Resolved reference"

      case _: SimpleValue    => "Basic data type"
      case value: HoconValue => value.valueType.mdName

      case _: HoconArray        => "Array"
      case _: HoconMergedValues => "Merge of values"
      case _: HoconObject       => "Object"
    }

    frag(b("Value type"), typeName)
  }

  lazy val renderer: ToMarkdown[MergedValuesDocument] = { document =>
    template(document) {
      frag(
        h3("Default value"),
        hoconResultRenderer.toMd(document.value.defaultValue),
        h3("Replacing value"),
        hoconResultRenderer.toMd(document.value.replacedValue),
      )
    }
  }
}
