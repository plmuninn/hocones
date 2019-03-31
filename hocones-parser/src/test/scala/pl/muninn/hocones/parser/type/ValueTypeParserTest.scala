package pl.muninn.hocones.parser.`type`

import pl.muninn.hocones.common.implicits._
import pl.muninn.hocones.parser.ParsingError
import pl.muninn.hocones.parser.entity._
import pl.muninn.hocones.parser.entity.simple.{EnvironmentValue, SimpleValue}

class ValueTypeParserTest extends ValueTypeParserTestSpec {

  behavior of "ValueTypeParser"

  it should "parse ConfigConcatenation to HoconConcatenation" in {
    val path = "pl.muninn.test.concatenation.value_1"
    val taggedPath = tagPath(path)
    val result = createValueTypeParserRun("concatenation.conf", path, ValueType.CONCATENATION).unsafeRunSync()
    result should matchPattern {
      case HoconConcatenation(`taggedPath`, _, _) =>
    }
    val resultValues = result.asInstanceOf[HoconConcatenation].value.values
    resultValues.size shouldBe 2
    val envValue = resultValues.head.asInstanceOf[EnvironmentValue]
    enviromentValueShouldHaveFields(envValue, "${?TEST}", "TEST", isOptional = true)
    val simpleValue = resultValues(1).asInstanceOf[SimpleValue]
    simpleValueShouldHaveFields(simpleValue, "/test", wasQuoted = true)
  }

  it should "parse ConfigDelayedMerge with arrays to HoconMergedValues" in {
    val path = "pl.muninn.test.merge.value_5"
    val taggedPath = tagPath(path)
    val result = createValueTypeParserRun("merge.conf", path, ValueType.MERGE).unsafeRunSync()
    result should matchPattern {
      case HoconMergedValues(`taggedPath`, _, _, _) =>
    }
    val resultValue = result.asInstanceOf[HoconMergedValues]

    val defaultValue = resultValue.defaultValue.asInstanceOf[HoconArray]
    defaultValue.path shouldBe taggedPath
    val defaultArrayItem = defaultValue.values.head.asInstanceOf[HoconValue]
    defaultArrayItem.valueType shouldBe SimpleValueType.QUOTED_STRING
    simpleValueShouldHaveFields(defaultArrayItem.value, "test", wasQuoted = true)

    val replacedValue = resultValue.replacedValue.asInstanceOf[HoconArray]
    replacedValue.path shouldBe taggedPath
    val replacedArrayItem = replacedValue.values.head.asInstanceOf[HoconEnvironmentValue]
    enviromentValueShouldHaveFields(replacedArrayItem.value, "${?TEST}", "TEST", isOptional = true)
  }

  it should "parse ConfigDelayedMerge with non-array values to HoconMergedValues" in {
    val path = "pl.muninn.test.merge.value_1"
    val taggedPath = tagPath(path)
    val result = createValueTypeParserRun("merge.conf", path, ValueType.MERGE).unsafeRunSync()
    result should matchPattern {
      case HoconMergedValues(`taggedPath`, _, _, _) =>
    }
    val resultValue = result.asInstanceOf[HoconMergedValues]

    val defaultValue = resultValue.defaultValue.asInstanceOf[HoconValue]
    defaultValue.path shouldBe taggedPath
    defaultValue.valueType shouldBe SimpleValueType.QUOTED_STRING
    simpleValueShouldHaveFields(defaultValue.value, "test", wasQuoted = true)

    val replacedValue = resultValue.replacedValue.asInstanceOf[HoconEnvironmentValue]
    replacedValue.path shouldBe taggedPath
    enviromentValueShouldHaveFields(replacedValue.value, "${TEST}", "TEST", isOptional = false)
  }

  it should "raise ParsingError when rendered value is not valid merged value" in {
    val path = "pl.muninn.test.concatenation.value_1"
    assertThrows[ParsingError](
      createValueTypeParserRun("concatenation.conf", path, ValueType.MERGE).unsafeRunSync()
    )
  }

  it should "parse ConfigReference with env value to HoconEnvironmentValue" in {
    val path = "pl.muninn.test.reference.value_1"
    val taggedPath = tagPath(path)
    val result = createValueTypeParserRun("reference.conf", path, ValueType.REFERENCE).unsafeRunSync()
    result should matchPattern {
      case HoconEnvironmentValue(`taggedPath`, _, _) =>
    }
    val resultValue = result.asInstanceOf[HoconEnvironmentValue]
    enviromentValueShouldHaveFields(resultValue.value, "${TEST_VALUE}", "TEST_VALUE", isOptional = false)
  }

  it should "parse ConfigReference with reference to HoconReferenceValue" in {
    val path = "pl.muninn.test.reference.value_4"
    val taggedPath = tagPath(path)
    val result = createValueTypeParserRun("reference.conf", path, ValueType.REFERENCE).unsafeRunSync()
    result should matchPattern {
      case HoconReferenceValue(`taggedPath`, _, _) =>
    }
    val resultValue = result.asInstanceOf[HoconReferenceValue]
    nonResolvedRefShouldHaveFields(resultValue.result, "${?pl.muninn.test.reference.value_1}",
      "pl.muninn.test.reference.value_1", isOptional = true)
  }

  it should "raise ParsingError when rendered value is not valid reference value" in {
    val path = "pl.muninn.test.simple.value_1"
    assertThrows[ParsingError](
      createValueTypeParserRun("simple.conf", path, ValueType.REFERENCE).unsafeRunSync()
    )
  }
}
