package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.MarkdownElement
import net.steppschuh.markdowngenerator.text.TextBuilder
import net.steppschuh.markdowngenerator.text.emphasis.{BoldText, ItalicText}
import pl.onewebpro.hocones.md.document.model._
import pl.onewebpro.hocones.meta.model.MetaValue
import pl.onewebpro.hocones.parser.entity.HoconResultValue

trait DocumentToMdGenerator[T <: Document[_]] extends MetaValueDocumentation {
  def toMd(document: T): MarkdownElement

  def heading(document: T): TextBuilder =
    new TextBuilder()
      .subHeading(document.metaInformation.name)
      .label("Path:")
      .text(new ItalicText(document.path))
      .newParagraph()

  implicit class TextBuilderOps(builder: TextBuilder) {

    def typeOfDocument(document: T): TextBuilder = {
      val bb = builder.label("Field Type:")
      (document match {
        case _: ArrayDocument             => bb.text("Array")
        case _: ConcatenationDocument     => bb.text("Concatenation")
        case _: EnvironmentDocument       => bb.text("Environment value")
        case _: MergedValuesDocument      => bb.text("Merge of values")
        case _: ObjectDocument            => bb.text("Object")
        case _: ReferenceValueDocument    => bb.text("Unresolved reference")
        case _: ResolvedReferenceDocument => bb.text("Reference")
        case _: ValueDocument             => bb.text("Value")
      }).newParagraph()
    }

    def description(document: T): TextBuilder =
      document.metaInformation.description
        .map({ desc =>
          builder.label("Description:").text(desc).newParagraph()
        })
        .getOrElse(builder)

    def metaInformation(metaInformation: MetaValue): TextBuilder = {
      val details = getDetails(Some(metaInformation))
      if (details.isEmpty) builder
      else {
        details
          .foldLeft(builder.label("Details:").newLine().beginList()) {
            case (listBuilder, (key, value)) =>
              listBuilder.text(s"$key: $value")
          }
          .end()
          .asInstanceOf[TextBuilder]
      }
    }

    def from(value: HoconResultValue): TextBuilder =
      builder
        .label("From file:")
        .text(value.cfg.origin().filename())
        .newParagraph()

    def label(title: String): TextBuilder =
      builder.text(new BoldText(title)).newLine()
  }

}
