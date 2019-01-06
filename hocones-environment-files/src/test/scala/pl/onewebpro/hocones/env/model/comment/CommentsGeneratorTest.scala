package pl.onewebpro.hocones.env.model.comment

import com.typesafe.config.{ConfigOrigin, ConfigValue}
import org.scalamock.scalatest.MockFactory
import pl.onewebpro.hocones.common.implicits.{Path => HPath}
import pl.onewebpro.hocones.env.TestSpec
import pl.onewebpro.hocones.env.model.comment.MetaInformationComments.{CanBeEmpty, Description, ElementType, MaxLength, MaxValue, MinLength, MinValue, Pattern, Type, ValueTypeEnum}
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue

class CommentsGeneratorTest extends TestSpec with MockFactory {

  trait ScalaConfigValue extends ConfigValue {
    override def origin(): ScalaConfigOrigin
  }

  trait ScalaConfigOrigin extends ConfigOrigin {
    def filename(): String
  }

  "CommentsGenerator.createMetaFields" should "return proper values for MetaObject" in {
    val value = MetaObject(
      name = "name",
      description = Some("description"),
      `element-type` = Some("elementType"),
    )

    val result: Iterable[Comment] =
      CommentsGenerator.createMetaFields(Some(value)).collect {
        case Some(v) => v
      }

    (result should contain).allOf(
      Type(ValueTypeEnum.Object),
      Description("description"),
      ElementType("elementType")
    )
  }

  "CommentsGenerator.createMetaFields" should "return proper values for MetaList" in {
    val value = MetaList(
      name = "name",
      description = Some("description"),
      `can-be-empty` = Some(true),
      `element-type` = Some("elementType"),
    )

    val result: Iterable[Comment] =
      CommentsGenerator.createMetaFields(Some(value)).collect {
        case Some(v) => v
      }

    (result should contain).allOf(
      Type(ValueTypeEnum.List),
      Description("description"),
      CanBeEmpty(true),
      ElementType("elementType")
    )
  }

  "CommentsGenerator.createMetaFields" should "return proper values for MetaNumber" in {
    val value = MetaNumber(
      name = "name",
      description = Some("description"),
      `min-value` = Some(1),
      `max-value` = Some(10)
    )

    val result: Iterable[Comment] =
      CommentsGenerator.createMetaFields(Some(value)).collect {
        case Some(v) => v
      }

    (result should contain).allOf(
      Type(ValueTypeEnum.Number),
      Description("description"),
      MinValue(1),
      MaxValue(10)
    )
  }

  "CommentsGenerator.createMetaFields" should "return proper values for MetaString" in {
    val value = MetaString(
      name = "name",
      description = Some("description"),
      pattern = Some("pattern"),
      `min-length` = Some(10),
      `max-length` = Some(100)
    )

    val result: Iterable[Comment] =
      CommentsGenerator.createMetaFields(Some(value)).collect {
        case Some(v) => v
      }

    (result should contain).allOf(
      Type(ValueTypeEnum.String),
      Description("description"),
      Pattern("pattern"),
      MinLength(10),
      MaxLength(100)
    )
  }

  "CommentsGenerator.createMetaFields" should "return proper values for MetaGenericInformation" in {
    val value = MetaGenericInformation(
      name = "name",
      description = Some("description")
    )

    val result: Iterable[Comment] =
      CommentsGenerator.createMetaFields(Some(value)).collect {
        case Some(v) => v
      }

    result should contain(Description("description"))
  }

  "CommentsGenerator.createMetaFields" should "return proper values for None" in {
    CommentsGenerator.createMetaFields(None).size shouldBe 0
  }

  "CommentsGenerator.createComments" should "return path, filename and isOptional comments" in {
    val path: HPath = "test.path.com"

    val configValue = mock[ScalaConfigValue]
    val origin = mock[ScalaConfigOrigin]
    (origin.filename _).expects().returning("fileName")
    (configValue.origin _).expects().returning(origin)

    val environmentValue = EnvironmentValue("${?VALUE}").unsafeRunSync()

    val results = CommentsGenerator.createComments(
      path = path,
      cfg = configValue,
      value = environmentValue,
      metaValue = None
    )

    (results should contain).allOf(
      Path(path),
      FileName("fileName"),
      IsOptional(true)
    )
  }

  "CommentsGenerator.createComments" should "merge default comments with meta information comments" in {
    val path: HPath = "test.path.com"

    val configValue = mock[ScalaConfigValue]
    val origin = mock[ScalaConfigOrigin]
    (origin.filename _).expects().returning("fileName")
    (configValue.origin _).expects().returning(origin)

    val environmentValue = EnvironmentValue("${?VALUE}").unsafeRunSync()

    val meta = MetaGenericInformation(
      name = "name",
      description = Some("description")
    )

    val results = CommentsGenerator.createComments(
      path = path,
      cfg = configValue,
      value = environmentValue,
      metaValue = Some(meta)
    )

    (results should contain).allOf(
      Path(path),
      FileName("fileName"),
      IsOptional(true),
      Description("description")
    )
  }
}
