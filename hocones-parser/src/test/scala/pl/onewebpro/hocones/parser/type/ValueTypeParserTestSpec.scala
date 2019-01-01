package pl.onewebpro.hocones.parser.`type`
import cats.effect.IO
import com.typesafe.config.Config
import pl.onewebpro.hocones.common.implicits.tagPath
import pl.onewebpro.hocones.parser.{HoconParser, TestSpec}
import pl.onewebpro.hocones.parser.`type`.ValueType.ValueType
import pl.onewebpro.hocones.parser.entity.HoconResultValue
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, SimpleValue}

trait ValueTypeParserTestSpec extends TestSpec {

  def createValueTypeParserRun(configFile: String, path: String, valueType: ValueType): IO[HoconResultValue] = {
    implicit val config: Config = loadConfig(configFile)
    val entries = getEntriesMap
    val value = entries(path)
    val renderedValue = HoconParser.render(value)
    val taggedPath = tagPath(path)
    ValueTypeParser.parse(taggedPath, valueType, renderedValue, value)
  }

  def enviromentValueShouldHaveFields(value: EnvironmentValue, env: String, name: String, isOptional: Boolean): Unit = {
    value.env.asInstanceOf[String] shouldBe env
    value.name.asInstanceOf[String] shouldBe name
    value.isOptional shouldBe isOptional
  }

  def nonResolvedRefShouldHaveFields(value: NotResolvedRef, env: String, name: String, isOptional: Boolean): Unit = {
    value.env.asInstanceOf[String] shouldBe env
    value.name.asInstanceOf[String] shouldBe name
    value.isOptional shouldBe isOptional
  }

  def simpleValueShouldHaveFields(simpleValue: SimpleValue, value: String, wasQuoted: Boolean): Unit = {
    simpleValue.value.asInstanceOf[String] shouldBe value
    simpleValue.wasQuoted shouldBe wasQuoted
  }
}
