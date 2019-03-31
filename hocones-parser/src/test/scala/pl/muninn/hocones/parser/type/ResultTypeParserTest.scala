package pl.muninn.hocones.parser.`type`
import com.typesafe.config.{Config, ConfigList}
import pl.muninn.hocones.common.implicits._
import pl.muninn.hocones.parser.{ParsingError, TestSpec}
import pl.muninn.hocones.parser.entity.{HoconArray, HoconObject, HoconValue}

import scala.collection.JavaConverters._

class ResultTypeParserTest extends TestSpec {

  behavior of "ResultTypeParser"

  it should "parse ConfigList to HoconArray" in {
    implicit val config: Config = loadConfig("array.conf")
    val path = "pl.muninn.test.array.value_1"
    val entries = getEntriesMap
    val value = entries(path)
    val taggedPath = tagPath(path)
    val result = ResultTypeParser.parse(taggedPath, ResultType.LIST, value).unsafeRunSync()
    result should matchPattern {
      case HoconArray(`taggedPath`, `value`, _) =>
    }
    val values = result.asInstanceOf[HoconArray].values.map(_.asInstanceOf[HoconValue]).sortWith(_.path < _.path)
    values.size shouldBe 5
    val expectedPaths = (0 to 4).map(i =>tagPath(s"pl.muninn.test.array.value_1.$i"))
    val expectedValueTypes = List(
      SimpleValueType.QUOTED_STRING,
      SimpleValueType.UNQUOTED_STRING,
      SimpleValueType.INT,
      SimpleValueType.DOUBLE,
      SimpleValueType.BOOLEAN
    )
    values.map(_.path) shouldBe expectedPaths
    values.map(_.valueType) shouldBe expectedValueTypes
  }

  it should "raise ParsingError when attempting to parse different Config as List" in {
    implicit val config: Config = loadConfig("object.conf")
    val path = "pl.muninn.test.object.value_1"
    val entries = getEntriesMap
    // retrieve first item of array which should be ConfigObject
    val value = entries(path).asInstanceOf[ConfigList].asScala.head
    val taggedPath = tagPath(path)
    assertThrows[ParsingError](ResultTypeParser.parse(taggedPath, ResultType.LIST, value).unsafeRunSync())
  }

  it should "parse ConfigObject to HoconObject" in {
    implicit val config: Config = loadConfig("object.conf")
    val path = "pl.muninn.test.object.value_1"
    val entries = getEntriesMap
    // retrieve first item of array which should be ConfigObject
    val value = entries(path).asInstanceOf[ConfigList].asScala.head
    val taggedPath = tagPath(path)
    val result = ResultTypeParser.parse(taggedPath, ResultType.OBJECT, value).unsafeRunSync()
    result should matchPattern {
      case HoconObject(`taggedPath`, `value`, _) =>
    }
    val values = result.asInstanceOf[HoconObject].values.map(_.asInstanceOf[HoconValue]).sortWith(_.path < _.path)
    values.size shouldBe 5
    val expectedPaths = (1 to 5).map(i =>tagPath(s"pl.muninn.test.object.value_1.value_1_1_$i"))
    val expectedValueTypes = List(
      SimpleValueType.UNQUOTED_STRING,
      SimpleValueType.QUOTED_STRING,
      SimpleValueType.INT,
      SimpleValueType.DOUBLE,
      SimpleValueType.BOOLEAN
    )
    values.map(_.path) shouldBe expectedPaths
    values.map(_.valueType) shouldBe expectedValueTypes
  }

  it should "raise ParsingError when attempting to parse different Config as Object" in {
    implicit val config: Config = loadConfig("object.conf")
    val path = "pl.muninn.test.object.value_1"
    val entries = getEntriesMap
    // value should be ConfigList
    val value = entries(path)
    val taggedPath = tagPath(path)
    assertThrows[ParsingError](ResultTypeParser.parse(taggedPath, ResultType.OBJECT, value).unsafeRunSync())
  }
}
