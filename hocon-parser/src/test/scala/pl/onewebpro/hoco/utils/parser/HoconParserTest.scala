package pl.onewebpro.hoco.utils.parser

import com.typesafe.config.Config
import pl.onewebpro.hocon.utils.parser.HoconParser
import pl.onewebpro.hocon.utils.parser.result.HoconResult

class HoconParserTest extends TestSpec {

  behavior of "HoconParser"

  it should "parse simple configurations" in {
    val config: Config = loadConfig("simple.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 7
    //TODO more tests
  }

  it should "parse concatenations in configuration" in {
    val config: Config = loadConfig("concatenation.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 9
    //TODO more tests
  }

  it should "parse reference values" in {
    val config: Config = loadConfig("reference.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 4
    //TODO more tests
  }

  it should "parse merge values" in {
    val config: Config = loadConfig("merge.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 7
    //TODO more tests
  }

  it should "parse array values" in {
    val config: Config = loadConfig("array.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 3
    //TODO more tests
  }

  it should "parse object values" in {
    val config: Config = loadConfig("object.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 2
    //TODO more tests
  }
}
