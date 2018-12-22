package pl.onewebpro.hocones.md.document.md

import net.steppschuh.markdowngenerator.text.TextBuilder
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple._

trait HoconMergedValuesOps {
  self: DocumentToMdGenerator[_]
    with HoconResultTypeOps
    with HoconEnvironmentValueOps
    with HoconReferenceOps
    with HoconResolvedReferenceOps
    with HoconMergedValuesOps
    with HoconValueOps
    with HoconConcatenationOps =>

  implicit class MergedValuesBuilderOps(builder: TextBuilder) {

    def composeValue(result: Result): TextBuilder =
      result match {
        case resultType: HoconResultType =>
          builder
            .environments(resultType)
            .references(resultType)
            .unresolvedReferences(resultType)

        case concatenation: HoconConcatenation =>
          builder
            .environments(concatenation)
            .references(concatenation)
            .unresolvedReferences(concatenation)

        case merged: HoconMergedValues =>
          builder.defaultValue(merged).replacedValue(merged)

        case reference: HoconReferenceValue => builder.referenceTo(reference)
        case resolvedReference: HoconResolvedReference =>
          builder.referenceTo(resolvedReference)

        case environment: HoconEnvironmentValue =>
          builder.name(environment).isOptional(environment)

        case value: HoconValue =>
          builder.typeOfValue(value).valueOfDocument(value)

        case composedConfigValue: ComposedConfigValue =>
          builder
            .environments(composedConfigValue)
            .references(composedConfigValue)
            .unresolvedReferences(composedConfigValue)

        case environmentValue: EnvironmentValue =>
          builder.name(environmentValue).isOptional(environmentValue)

        case simpleValue: SimpleValue => builder.valueOfDocument(simpleValue)

        case notResolvedRef: NotResolvedRef =>
          builder.referenceTo(notResolvedRef)

        case resolvedRef: ResolvedRef =>
          builder.referenceTo(resolvedRef.reference)
      }

    def typeOfResultValue(result: Result): TextBuilder = {
      val bb = builder.label("Value Type:")

      (result match {
        case _: HoconArray             => bb.text("Array")
        case _: HoconObject            => bb.text("Object")
        case _: HoconConcatenation     => bb.text("Concatenation")
        case _: HoconMergedValues      => bb.text("Merge of values")
        case _: HoconReferenceValue    => bb.text("Unresolved reference")
        case _: HoconResolvedReference => bb.text("Reference")
        case _: HoconEnvironmentValue  => bb.text("Environment value")
        case _: HoconValue             => bb.text("Value")
        case _: ComposedConfigValue    => bb.text("Concatenation")
        case _: EnvironmentValue       => bb.text("Environment value")
        case _: SimpleValue            => bb.text("Value")
        case _: NotResolvedRef         => bb.text("Unresolved reference")
        case _: ResolvedRef            => bb.text("Array")
      }).newParagraph()
    }

    def defaultValue(result: Result): TextBuilder =
      builder
        .heading("Default value", 3)
        .typeOfResultValue(result)
        .composeValue(result)
        .newParagraph()

    def replacedValue(result: Result): TextBuilder =
      builder
        .heading("Replacing value", 3)
        .typeOfResultValue(result)
        .composeValue(result)
        .newParagraph()
  }

}
