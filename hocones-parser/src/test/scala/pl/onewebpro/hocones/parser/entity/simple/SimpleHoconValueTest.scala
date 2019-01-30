package pl.onewebpro.hocones.parser.entity.simple
import pl.onewebpro.hocones.parser.TestSpec

class SimpleHoconValueTest extends TestSpec {

  behavior of "SimpleHoconValue"

  it should "create NotResolvedRef from matching string" in {
    SimpleHoconValue("${test.my.conf.value}").unsafeRunSync() should matchPattern {
      case NotResolvedRef(_, _, _) =>
    }
  }

  it should "create ComposedConfigValue from matching string" in {
    SimpleHoconValue("${test_value}\"/test\"").unsafeRunSync() should matchPattern {
      case ComposedConfigValue(_, _) =>
    }
  }

  it should "create EnvironmentValue from matching string" in {
    SimpleHoconValue("${test}").unsafeRunSync() should matchPattern {
      case EnvironmentValue(_, _, _) =>
    }
  }

  it should "create SimpleValue from remaining strings" in {
    SimpleHoconValue("{test}").unsafeRunSync() should matchPattern {
      case SimpleValue(_, _) =>
    }
  }
}
