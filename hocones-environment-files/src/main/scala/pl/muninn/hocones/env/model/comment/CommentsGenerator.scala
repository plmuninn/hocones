package pl.muninn.hocones.env.model.comment

import com.typesafe.config.ConfigValue
import pl.muninn.hocones.parser.entity.simple.EnvironmentValue
import pl.muninn.hocones.common.implicits.{Path => HPath}
import pl.muninn.hocones.env.model.comment.MetaInformationComments.{CanBeEmpty, Description, ElementType, MaxLength, MaxValue, MinLength, MinValue, Pattern, Type, ValueTypeEnum}
import pl.muninn.hocones.meta.model.{MetaGenericInformation, MetaList, MetaNumber, MetaObject, MetaString, MetaValue}

object CommentsGenerator {

  private[comment] def createMetaFields: Option[MetaValue] => Iterable[Option[Comment]] = {
    case Some(value: MetaObject) =>
      Iterable(
        Some(Type(ValueTypeEnum.Object)),
        value.description.map(Description),
        value.`element-type`.map(ElementType)
      )
    case Some(value: MetaList) =>
      Iterable(
        Some(Type(ValueTypeEnum.List)),
        value.description.map(Description),
        value.`can-be-empty`.map(CanBeEmpty),
        value.`element-type`.map(ElementType)
      )
    case Some(value: MetaNumber) =>
      Iterable(
        Some(Type(ValueTypeEnum.Number)),
        value.description.map(Description),
        value.`min-value`.map(MinValue),
        value.`max-value`.map(MaxValue)
      )
    case Some(value: MetaString) =>
      Iterable(
        Some(Type(ValueTypeEnum.String)),
        value.description.map(Description),
        value.pattern.map(Pattern),
        value.`min-length`.map(MinLength),
        value.`max-length`.map(MaxLength)
      )
    case Some(value: MetaGenericInformation) =>
      Iterable(value.description.map(Description))
    case Some(value: MetaValue) =>
      Iterable(value.description.map(Description))
    case _ => Iterable()
  }

  def createComments(
    path: HPath,
    cfg: ConfigValue,
    value: EnvironmentValue,
    metaValue: Option[MetaValue]
  ): Iterable[Comment] = {
    val metaFieldComments: Iterable[Option[Comment]] = createMetaFields(metaValue)

    val baseFieldsComments: Iterable[Option[Comment]] =
      Iterable(
        Some(Path(path)),
        Option(cfg.origin().filename()).map(FileName),
        Some(IsOptional(value.isOptional))
      )

    (baseFieldsComments ++ metaFieldComments).flatten
  }

}
