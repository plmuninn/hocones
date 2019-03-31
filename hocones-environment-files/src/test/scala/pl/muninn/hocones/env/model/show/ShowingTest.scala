package pl.muninn.hocones.env.model.show

import cats.implicits._
import pl.muninn.hocones.common.DefaultValue.{DefaultValue, tagDefaultValue}
import pl.muninn.hocones.env.model.EnvironmentValue
import pl.muninn.hocones.env.model.comment.MetaInformationComments.Description
import pl.muninn.hocones.env.{TestSpec, model}
import pl.muninn.hocones.env.model.comment.{FileName, IsOptional, Path}

class ShowingTest extends TestSpec {
  "showBoolean" should "show yes or no for boolean values" in {
    showBoolean.show(true) shouldBe "yes"
    showBoolean.show(false) shouldBe "no"
  }

  "showPath" should "show Path properly" in {
    Path("pl.com.org.net").show shouldBe "Path: pl.com.org.net"
  }

  "showFileName" should "show FileName properly" in {
    FileName("fileName").show shouldBe "File name: fileName"
  }

  "showIsOptional" should "show IsOptional properly" in {
    IsOptional(true).show shouldBe "Optional: yes"
    IsOptional(false).show shouldBe "Optional: no"
  }

  "showName" should "show Name properly" in {
    model.tagName("VALUE").show shouldBe "VALUE="
  }

  "showDefaultValue" should "show default value properly" in {
    Option(tagDefaultValue("1234")).show shouldBe "1234"
    Option.empty[DefaultValue].show shouldBe ""
  }

  "showComment" should "show comment with prefix" in {
    showComment.show(FileName("fileName")) shouldBe "# File name: fileName"
    showComment.show(Path("pl.com.org.net")) shouldBe "# Path: pl.com.org.net"
    showComment.show(Description("Some description")) shouldBe "# Description: Some description"
  }

  "showEnvironmentValue" should "show EnvironmentValue properly" in {

    EnvironmentValue(
      name = model.tagName("VALUE"),
      defaultValue = Option(
        tagDefaultValue("1234")
      ),
      comments = Iterable(
        IsOptional(true),
        Path("pl.com.org.net"),
        FileName("fileName"),
        Description("Some description")
      )
    ).show.trim shouldBe
    """
        |# Optional: yes
        |# Path: pl.com.org.net
        |# File name: fileName
        |# Description: Some description
        |VALUE=1234
      """.stripMargin.trim
  }

}
