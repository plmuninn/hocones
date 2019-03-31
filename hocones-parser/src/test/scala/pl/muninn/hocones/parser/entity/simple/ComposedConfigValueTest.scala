package pl.muninn.hocones.parser.entity.simple
import pl.muninn.hocones.parser.{ParsingError, TestSpec}

class ComposedConfigValueTest extends TestSpec {
  import org.scalatest.Inspectors._

  case class TestData(value: String, extracted: Int)

  val validCases = Seq(
    TestData("""${TEST}"/test"""", 2),
    TestData(""""/test/"${TEST}""", 2),
    TestData("""${TEST}"/test/"${TEST}""", 3),
    TestData("""${TEST}"/test/"${TEST}"/test/"""", 4),
    TestData("""${TEST}/test/${TEST}"/test/"""", 4)
  )

  val invalidCases = Seq(
    "${?TEST}",
    "/test",
    "$TEST/test"
  )

  behavior of "ComposedConfigValue"

  it should "check if value is composed value" in {
    forEvery (validCases) { tc =>
      ComposedConfigValue.isComposedValue(tc.value) shouldBe true
    }
    forEvery (invalidCases) { value =>
      ComposedConfigValue.isComposedValue(value) shouldBe false
    }
  }

  it should "extract values from composed value" in {
    forEvery (validCases) { tc =>
      val result = ComposedConfigValue.extractValues(tc.value).unsafeRunSync()
      result.size shouldBe tc.extracted
    }
  }

  it should "create ComposedConfigValue from composed value" in {
    forEvery (validCases) { tc =>
      val result = ComposedConfigValue.apply(tc.value).unsafeRunSync()
      result.pattern.asInstanceOf[String] shouldBe tc.value
      result.values.size shouldBe tc.extracted
    }
  }

  it should "raise ParsingError when trying to create ComposedConfigValue from invalid value" in {
    assertThrows[ParsingError](ComposedConfigValue.apply(invalidCases.head).unsafeRunSync())
  }
}
