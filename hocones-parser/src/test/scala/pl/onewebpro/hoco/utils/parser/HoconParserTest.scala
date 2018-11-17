package pl.onewebpro.hoco.utils.parser

import com.typesafe.config.Config
import pl.onewebpro.hocones.parser.{HoconParser, HoconResult}

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
    result.results.size shouldBe 5
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

  it should "parse complex example" in {
    val config: Config = loadConfig("parser.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    result.results.size shouldBe 33
    //TODO more tests
  }

  it should "return 7 results for simple.conf file flattened" in {
    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("simple.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()

    result.results.flattenResultValues(true).size shouldBe 7
  }

  it should "return 4 results for merge.conf file flattened" in {
    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("merge.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()

    result.results.flattenResultValues(true).size shouldBe 7
  }

  it should "return 18 results for array.conf file flattened" in {
    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("array.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()

    result.results.flattenResultValues(true).size shouldBe 23
  }

  it should "return 16 results for object.conf file flattened" in {
    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("object.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()

    result.results.flattenResultValues(true).size shouldBe 24
  }

  it should "find correctly values from array.conf file" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("array.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()

    val resultList = result.results.flattenResultValues(true)

    resultList.get("pl.onewebpro.test.array.value_1").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.array.value_2.3").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.array.value_3.1.1").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.array.value_3.5").isDefined shouldBe false
  }

  it should "find correctly values from object.conf file" in {
    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("object.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()

    val resultList = result.results.flattenResultValues(true)


    resultList.get("pl.onewebpro.test.object.reference").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.object.value_1").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.object.value_1.0.value_1_1_1").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.object.value_1.4.value_1_5_1").isDefined shouldBe true
    resultList.get("pl.onewebpro.test.object.value_1.4.value_1_5_1.0").isDefined shouldBe true

    resultList.get("pl.onewebpro.test.object.value_1.4.value_1_5_1.10").isDefined shouldBe false
  }

  it should "extract proper env hocones values from simple.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("simple.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.containsEnvironmentValues

    extractonResult.size shouldBe 0
  }

  it should "extract proper env hocones values from reference.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("reference.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.containsEnvironmentValues

    extractonResult.size shouldBe 2
  }

  it should "extract proper env hocones values from concatenation.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("concatenation.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.containsEnvironmentValues

    extractonResult.size shouldBe 9
  }

  it should "extract proper env hocones values from merge.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("merge.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.containsEnvironmentValues

    extractonResult.size shouldBe 6
  }

  it should "extract proper env hocones values from array.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("array.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.containsEnvironmentValues

    extractonResult.size shouldBe 4
  }

  it should "extract proper env hocones values from object.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("object.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.containsEnvironmentValues

    extractonResult.size shouldBe 5
  }


  it should "extract proper env values from simple.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("simple.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.environmentValues

    extractonResult.size shouldBe 0
  }

  it should "extract proper env values from reference.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("reference.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.environmentValues

    extractonResult.size shouldBe 2
  }

  it should "extract proper env values from concatenation.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("concatenation.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.environmentValues

    extractonResult.size shouldBe 2
  }

  it should "extract proper env values from merge.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("merge.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.environmentValues

    extractonResult.size shouldBe 2
  }

  it should "extract proper env values from array.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("array.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.environmentValues

    extractonResult.size shouldBe 2
  }

  it should "extract proper env values from object.conf" in {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val config: Config = loadConfig("object.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val extractonResult = result.results.environmentValues

    extractonResult.size shouldBe 2
  }
}
