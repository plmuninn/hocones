package pl.onewebpro.hocones.parser.ops

import com.typesafe.config.ConfigValue
import org.scalamock.scalatest.MockFactory
import pl.onewebpro.hocones.parser.TestSpec
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{NotResolvedRef, ResolvedRef, SimpleValue, EnvironmentValue => HEnvironmentValue}

class DefaultValueTest extends TestSpec with MockFactory {

  trait ScalaConfigValue extends ConfigValue {}

  val configValue: ConfigValue = mock[ScalaConfigValue]

  "DefaultValue.extractDefaultValue" should "extract values properly" in {
    val array = HoconArray("path", configValue, Nil)
    val simpleValue = SimpleValue("1234")
    val hoconValue = HoconValue("path", configValue, SimpleValueType.UNQUOTED_STRING, simpleValue)
    val notResolvedRef = NotResolvedRef("${?path.value}").unsafeRunSync()
    val resolvedRef = ResolvedRef(simpleValue, notResolvedRef)
    val referenceValue = HoconReferenceValue("path", configValue, notResolvedRef)
    val hoconResolvedReference = HoconResolvedReference(hoconValue, referenceValue)

    DefaultValue.extractDefaultValue(array).isDefined shouldBe false

    DefaultValue.extractDefaultValue(simpleValue).isDefined shouldBe true
    DefaultValue.extractDefaultValue(simpleValue).get shouldBe "1234"

    DefaultValue.extractDefaultValue(hoconValue).isDefined shouldBe true
    DefaultValue.extractDefaultValue(hoconValue).get shouldBe "1234"

    DefaultValue.extractDefaultValue(notResolvedRef).isDefined shouldBe false

    DefaultValue.extractDefaultValue(resolvedRef).isDefined shouldBe true
    DefaultValue.extractDefaultValue(resolvedRef).get shouldBe "1234"
    DefaultValue.extractDefaultValue(hoconResolvedReference).isDefined shouldBe true
    DefaultValue.extractDefaultValue(hoconResolvedReference).get shouldBe "1234"
  }

  "DefaultValue.createDefaultValue" should "extract values properly" in {
    val array = HoconArray("path", configValue, Nil)

    DefaultValue.createDefaultValue(array).isDefined shouldBe false

    val simpleValue = SimpleValue("1234")
    val hoconEnvironmentValue = HoconEnvironmentValue("123", configValue, HEnvironmentValue("${VALUE}").unsafeRunSync())

    val mergedValues = HoconMergedValues("path", configValue, simpleValue, hoconEnvironmentValue)

    DefaultValue.createDefaultValue(mergedValues).isDefined shouldBe true
    DefaultValue.createDefaultValue(mergedValues).get shouldBe "1234"
  }

}
