package pl.onewebpro.hocones.parser.entity.simple
import pl.onewebpro.hocones.parser.{ParsingError, TestSpec}

class NotResolvedRefTest extends TestSpec {
  import org.scalatest.Inspectors._

  case class ValidTestData(value: String, refValue:String, refName: String, isOptional: Boolean)

  val validCases = Seq(
    ValidTestData("${pl.test}", "${pl.test}", "pl.test", isOptional = false),
    ValidTestData("${pl.onewebpro.value}", "${pl.onewebpro.value}", "pl.onewebpro.value", isOptional = false),
    ValidTestData("${?pl.onewebpro.value}", "${?pl.onewebpro.value}", "pl.onewebpro.value", isOptional = true),
    ValidTestData("${pl.onewebpro.value.0}", "${pl.onewebpro.value.0}", "pl.onewebpro.value.0", isOptional = false)
  )

  val invalidCases = Seq(
    "${TEST}",
    "${?TEST}",
    "${?test}",
    "{pl.test}",
    "${pl.test",
    "$pl.test}",
    "$pl.test",
    "pl.onewebpro.test",
  )

  behavior of "NotResolvedRef"

  it should "detect if value isRef" in {
    forEvery (validCases) { tc =>
      NotResolvedRef.isRef(tc.value) shouldBe true
    }
    forEvery (invalidCases) { value =>
      NotResolvedRef.isRef(value) shouldBe false
    }
  }

  it should "create NotResolvedRef from string" in {
    forEvery (validCases) { tc =>
      val result = NotResolvedRef(tc.value).unsafeRunSync()
      result.env.asInstanceOf[String] shouldBe tc.refValue
      result.name.asInstanceOf[String] shouldBe tc.refName
      result.isOptional shouldBe tc.isOptional
    }
  }

  it should "raise ParsingError when trying to NotResolvedRef from invalid string" in {
    forEvery (invalidCases) { value =>
      assertThrows[ParsingError](NotResolvedRef(value).unsafeRunSync())
    }
  }

}
