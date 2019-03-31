package pl.muninn.hocones.parser.entity.simple
import pl.muninn.hocones.parser.TestSpec

class SimpleValueTest extends TestSpec {
  import org.scalatest.Inspectors._

  case class TestData(value: String, extracted: String, isQuoted: Boolean)

  val testCases = Seq(
    TestData("\"\"", "", isQuoted = true),
    TestData("\"test\"", "test", isQuoted = true),
    TestData("\"test\"test\"", "test\"test", isQuoted = true),
    TestData("\"test\"\"test\"", "test\"\"test", isQuoted = true),
    TestData("\"test\"test", "\"test\"test", isQuoted = false),
    TestData("test\"test\"", "test\"test\"", isQuoted = false),
    TestData("test\"\"test", "test\"\"test", isQuoted = false),
    TestData("\"", "\"", isQuoted = false),
    TestData("", "", isQuoted = false),
    TestData("7", "7", isQuoted = false),
    TestData("0.123", "0.123", isQuoted = false),
  )

  behavior of "SimpleValue"

  it should "detect if value is quoted" in {
    forEvery (testCases) { tc =>
      SimpleValue.isQuotedValue(tc.value) shouldBe tc.isQuoted
    }
  }

  it should "extract value is quoted" in {
    forEvery (testCases) { tc =>
      SimpleValue.extractIfQuotedValue(tc.value) shouldBe tc.extracted
    }
  }

  it should "create SimpleValue instance from string" in {
    forEvery (testCases) { tc =>
      val result = SimpleValue(tc.value)
      result.value.asInstanceOf[String] shouldBe tc.extracted
      result.wasQuoted shouldBe tc.isQuoted
    }
  }
}
