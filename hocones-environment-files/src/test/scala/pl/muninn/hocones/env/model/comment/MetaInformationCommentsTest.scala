package pl.muninn.hocones.env.model.comment

import cats.implicits._
import pl.muninn.hocones.env.TestSpec
import pl.muninn.hocones.env.model.comment.MetaInformationComments.{CanBeEmpty, Description, ElementType, MaxLength, MaxValue, MinLength, MinValue, Pattern, Type, ValueTypeEnum}

class MetaInformationCommentsTest extends TestSpec {

  "MetaInformationComments.showType" should "show properly Type" in {
    import MetaInformationComments.showType

    Type(ValueTypeEnum.Object).show shouldBe "Type: Object"
    Type(ValueTypeEnum.List).show shouldBe "Type: List"
    Type(ValueTypeEnum.Number).show shouldBe "Type: Number"
    Type(ValueTypeEnum.String).show shouldBe "Type: String"
  }

  "MetaInformationComments.showDescription" should "show properly Description" in {
    import MetaInformationComments.showDescription

    Description("Some description").show shouldBe "Description: Some description"
  }

  "MetaInformationComments.showElementType" should "show properly ElementType" in {
    import MetaInformationComments.showElementType

    ElementType("string").show shouldBe "Element type: string"
  }

  "MetaInformationComments.showCanBeEmpty" should "show properly CanBeEmpty" in {
    import MetaInformationComments.showCanBeEmpty

    CanBeEmpty(true).show shouldBe "Can be empty: yes"
    CanBeEmpty(false).show shouldBe "Can be empty: no"
  }

  "MetaInformationComments.showPattern" should "show properly Pattern" in {
    import MetaInformationComments.showPattern

    Pattern("http://<service_name>.example.pl").show shouldBe "Pattern: http://<service_name>.example.pl"
  }

  "MetaInformationComments.showMinValue" should "show properly MinValue" in {
    import MetaInformationComments.showMinValue

    MinValue(1).show shouldBe "Minimum value: 1"
  }

  "MetaInformationComments.showMaxValue" should "show properly MaxValue" in {
    import MetaInformationComments.showMaxValue

    MaxValue(1).show shouldBe "Maximum value: 1"
  }

  "MetaInformationComments.showMinLength" should "show properly MinLength" in {
    import MetaInformationComments.showMinLength

    MinLength(1).show shouldBe "Minimum length: 1"
  }

  "MetaInformationComments.showMaxLength" should "show properly MaxLength" in {
    import MetaInformationComments.showMaxLength

    MaxLength(1).show shouldBe "Maximum length: 1"
  }
}
