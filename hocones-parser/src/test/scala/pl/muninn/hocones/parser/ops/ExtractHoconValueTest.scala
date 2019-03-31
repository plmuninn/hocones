package pl.muninn.hocones.parser.ops
import com.typesafe.config.ConfigValue
import org.scalamock.scalatest.MockFactory
import pl.muninn.hocones.common.implicits._
import pl.muninn.hocones.parser.TestSpec
import pl.muninn.hocones.parser.`type`.SimpleValueType
import pl.muninn.hocones.parser.entity._
import pl.muninn.hocones.parser.entity.simple._

class ExtractHoconValueTest extends TestSpec with MockFactory {

  behavior of "ExtractHoconValue.isValue"

  it should "detect SimpleValue" in new Context {
    ExtractHoconValue.isValue[SimpleValue](simpleValue) shouldBe true
    ExtractHoconValue.isValue[SimpleValue](composedConfigValue) shouldBe true
    ExtractHoconValue.isValue[SimpleValue](environmentValue) shouldBe false
  }

  it should "detect NotResolvedRef" in new Context {
    ExtractHoconValue.isValue[NotResolvedRef](composedConfigValue) shouldBe true
    ExtractHoconValue.isValue[NotResolvedRef](notResolvedRef) shouldBe true
    ExtractHoconValue.isValue[NotResolvedRef](environmentValue) shouldBe false
  }

  it should "detect ResolvedRef" in new Context {
    ExtractHoconValue.isValue[ResolvedRef](resolvedRef) shouldBe true
    ExtractHoconValue.isValue[ResolvedRef](composedConfigValue) shouldBe false
  }

  it should "detect EnvironmentValue" in new Context {
    ExtractHoconValue.isValue[EnvironmentValue](environmentValue) shouldBe true
    ExtractHoconValue.isValue[EnvironmentValue](composedConfigValue) shouldBe false
    ExtractHoconValue.isValue[EnvironmentValue](notResolvedRef) shouldBe false
  }

  it should "detect ComposedConfigValue" in new Context {
    ExtractHoconValue.isValue[ComposedConfigValue](composedConfigValue) shouldBe true
    ExtractHoconValue.isValue[ComposedConfigValue](simpleValue) shouldBe false
  }

  behavior of "ExtractHoconValue.resultContainsValue"

  it should "handle HoconConcatenation" in new Context {
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconConcatenation) shouldBe true
    ExtractHoconValue.resultContainsValue[EnvironmentValue](hoconConcatenation) shouldBe false
  }

  it should "handle HoconMergedValues" in new Context {
    ExtractHoconValue.resultContainsValue[SimpleValue](hoconMergedValues) shouldBe true
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconMergedValues) shouldBe true
    ExtractHoconValue.resultContainsValue[EnvironmentValue](hoconMergedValues) shouldBe false
  }

  it should "handle HoconArray" in new Context {
    ExtractHoconValue.resultContainsValue[EnvironmentValue](hoconArray) shouldBe true
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconArray) shouldBe true
    ExtractHoconValue.resultContainsValue[SimpleValue](hoconArray) shouldBe false
  }

  it should "handle HoconObject" in new Context {
    ExtractHoconValue.resultContainsValue[EnvironmentValue](hoconObject) shouldBe true
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconObject) shouldBe true
    ExtractHoconValue.resultContainsValue[SimpleValue](hoconObject) shouldBe false
  }

  it should "handle HoconReferenceValue" in new Context {
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconReferenceValue) shouldBe true
    ExtractHoconValue.resultContainsValue[EnvironmentValue](hoconReferenceValue) shouldBe false
  }

  it should "handle HoconEnvironmentValue" in new Context {
    ExtractHoconValue.resultContainsValue[EnvironmentValue](hoconEnvironmentValue) shouldBe true
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconEnvironmentValue) shouldBe false
  }

  it should "handle HoconValue" in new Context {
    ExtractHoconValue.resultContainsValue[SimpleValue](hoconValue) shouldBe true
    ExtractHoconValue.resultContainsValue[NotResolvedRef](hoconValue) shouldBe false
  }

  it should "handle simple values" in new Context {
    ExtractHoconValue.resultContainsValue[EnvironmentValue](environmentValue) shouldBe false
    ExtractHoconValue.resultContainsValue[SimpleValue](simpleValue) shouldBe false
  }

  behavior of "ExtractHoconValue.extractValues"

  it should "handle HoconConcatenation" in new Context {
    ExtractHoconValue.extractValues[NotResolvedRef](hoconConcatenation) shouldBe Iterable(notResolvedRef)
    ExtractHoconValue.extractValues[EnvironmentValue](hoconConcatenation) shouldBe Nil
  }

  it should "handle HoconMergedValues" in new Context {
    val mergedValuesWithSimple: HoconMergedValues = hoconMergedValues.copy(defaultValue = simpleValue)
    ExtractHoconValue.extractValues[SimpleValue](mergedValuesWithSimple) shouldBe Iterable(simpleValue)
    ExtractHoconValue.extractValues[NotResolvedRef](hoconMergedValues) shouldBe Iterable(notResolvedRef)
    ExtractHoconValue.extractValues[EnvironmentValue](mergedValuesWithSimple) shouldBe Nil
    ExtractHoconValue.extractValues[EnvironmentValue](hoconMergedValues) shouldBe Nil
  }

  it should "handle HoconArray" in new Context {
    ExtractHoconValue.extractValues[NotResolvedRef](hoconArray) shouldBe Iterable(notResolvedRef, notResolvedRef)
    ExtractHoconValue.extractValues[SimpleValue](hoconArray) shouldBe Nil
  }

  it should "handle HoconObject" in new Context {
    ExtractHoconValue.extractValues[NotResolvedRef](hoconObject) shouldBe Iterable(notResolvedRef, notResolvedRef)
    ExtractHoconValue.extractValues[SimpleValue](hoconObject) shouldBe Nil
  }

  it should "handle HoconEnvironmentValue" in new Context {
    ExtractHoconValue.extractValues[EnvironmentValue](hoconEnvironmentValue) shouldBe Iterable(environmentValue)
    ExtractHoconValue.extractValues[SimpleValue](hoconEnvironmentValue) shouldBe Nil
  }

  it should "handle HoconValue" in new Context {
    ExtractHoconValue.extractValues[SimpleValue](hoconValue) shouldBe Iterable(simpleValue)
    ExtractHoconValue.extractValues[EnvironmentValue](hoconValue) shouldBe Nil
  }

  it should "handle HoconReferenceValue" in new Context {
    ExtractHoconValue.extractValues[NotResolvedRef](hoconReferenceValue) shouldBe Iterable(notResolvedRef)
    ExtractHoconValue.extractValues[SimpleValue](hoconReferenceValue) shouldBe Nil
  }

  trait Context {
    val path: Path = tagPath("pl.muninn.test")
    val cfg: ConfigValue = mock[ConfigValue]
    val simpleValue = SimpleValue("1")
    val hoconValue = HoconValue("pl.test.sample.value", cfg, SimpleValueType.INT, simpleValue)
    val composedConfigValue: ComposedConfigValue =
      ComposedConfigValue("""${pl.test.sample.value}"/test"""").unsafeRunSync()
    val environmentValue: EnvironmentValue = EnvironmentValue("${TEST}").unsafeRunSync()
    val notResolvedRef: NotResolvedRef = NotResolvedRef("${pl.test.sample.value}").unsafeRunSync()
    val resolvedRef = ResolvedRef(hoconValue, notResolvedRef)

    val hoconConcatenation = HoconConcatenation(path, cfg, composedConfigValue)
    val hoconReferenceValue = HoconReferenceValue("pl.test.config.value1", cfg, notResolvedRef)
    val hoconMergedValues = HoconMergedValues(path, cfg, hoconValue, hoconReferenceValue)
    val hoconEnvironmentValue = HoconEnvironmentValue(path, cfg, environmentValue)
    val hoconArray = HoconArray(path, cfg, Seq(hoconEnvironmentValue, hoconReferenceValue, hoconReferenceValue))
    val hoconObject = HoconObject(path, cfg, Seq(hoconEnvironmentValue, hoconReferenceValue, hoconReferenceValue))
  }

}
