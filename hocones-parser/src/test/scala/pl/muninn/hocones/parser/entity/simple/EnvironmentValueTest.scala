package pl.muninn.hocones.parser.entity.simple

import org.scalatest.OptionValues
import pl.muninn.hocones.parser.{ParsingError, TestSpec}

class EnvironmentValueTest extends TestSpec with OptionValues {

  import org.scalatest.Inspectors._

  case class TestData(value: String, env: String, name: String, isOptional: Boolean)

  val validCases = Seq(
    TestData("${TEST}", "${TEST}", "TEST", isOptional = false),
    TestData("${TEST_DATA}", "${TEST_DATA}", "TEST_DATA", isOptional = false),
    TestData("${?TEST}", "${?TEST}", "TEST", isOptional = true),
    TestData("${?test}", "${?test}", "test", isOptional = true),
  )

  val invalidCases = Seq(
    "$TEST",
    "TEST",
    "{TEST}"
  )

  behavior of "EnvironmentValue"

  it should "check if string value is env" in {
    forEvery (validCases) { tc =>
      EnvironmentValue.isEnv(tc.value) shouldBe true
    }
    forEvery (invalidCases) { value =>
      EnvironmentValue.isEnv(value) shouldBe false
    }
  }

  it should "check if string value contains env" in {
    forEvery (validCases) { tc =>
      EnvironmentValue.containsEnv(tc.value) shouldBe true
    }
    forEvery (invalidCases) { value =>
      EnvironmentValue.containsEnv(value) shouldBe false
    }
  }

  it should "create EnvValue from valid string value" in {
    forEvery (validCases) { tc =>
      val result = EnvironmentValue.envName(tc.value)
      result.value.asInstanceOf[String] shouldBe tc.env
    }
    forEvery (invalidCases) { value =>
      val result = EnvironmentValue.envName(value)
      result shouldBe None
    }
  }

  it should "check if EnvValue is optional" in {
    forEvery (validCases) { tc =>
      val envValue = EnvironmentValue.envName(tc.value).value
      EnvironmentValue.isOptionalEnv(envValue) shouldBe tc.isOptional
    }
  }

  it should "extract EnvName from EnvValue" in {
    forEvery (validCases) { tc =>
      val envValue = EnvironmentValue.envName(tc.value).value
      val result = EnvironmentValue.extractName(envValue)
      result.value.asInstanceOf[String] shouldBe tc.name
    }
  }

  it should "create EnvironmentValue from valid string value" in {
    forEvery (validCases) { tc =>
      val result = EnvironmentValue.apply(tc.value).unsafeRunSync()
      result.env.asInstanceOf[String] shouldBe tc.env
      result.name.asInstanceOf[String] shouldBe tc.name
      result.isOptional shouldBe tc.isOptional
    }
  }

  it should "raise ParsingError when trying to create EnvironmentValue from invalid string value" in {
    assertThrows[ParsingError](EnvironmentValue.apply(invalidCases.head).unsafeRunSync())
  }

  it should "create EnvironmentValue from NotResolvedRef" in {
    val notResolvedRef = NotResolvedRef("${pl.muninn.value}").unsafeRunSync()
    val result = EnvironmentValue.apply(notResolvedRef)
    result.env.asInstanceOf[String] shouldBe "${value}"
    result.name.asInstanceOf[String] shouldBe "value"
    result.isOptional shouldBe false
  }
}
