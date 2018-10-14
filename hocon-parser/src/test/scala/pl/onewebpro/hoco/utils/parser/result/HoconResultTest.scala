package pl.onewebpro.hoco.utils.parser.result

//import com.typesafe.config.Config
import pl.onewebpro.hoco.utils.parser.TestSpec
//import pl.onewebpro.hocon.utils.parser.HoconParser
//import pl.onewebpro.hocon.utils.parser.result.HoconResult

class HoconResultTest extends TestSpec {

//  behavior of "HoconResult.flatten"
//
//  it should "return 7 results for simple.conf file" in {
//    val config: Config = loadConfig("simple.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    result.flatten().size shouldBe 7
//  }
//
//  it should "return 4 results for merge.conf file" in {
//    val config: Config = loadConfig("merge.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    result.flatten().size shouldBe 4
//  }
//
//  it should "return 18 results for array.conf file" in {
//    val config: Config = loadConfig("array.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    result.flatten().size shouldBe 23
//  }
//
//  it should "return 16 results for object.conf file" in {
//    val config: Config = loadConfig("object.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    result.flatten().size shouldBe 24
//  }
//
//  behavior of "HoconResult.find"
//
//  it should "return correctly values from array.conf file" in {
//    val config: Config = loadConfig("array.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//
//    result.find(HoconParser.tagPath("pl.onewebpro.test.array.value_1")).isDefined shouldBe true
//    result.find(HoconParser.tagPath("pl.onewebpro.test.array.value_2.3")).isDefined shouldBe true
//    result.find(HoconParser.tagPath("pl.onewebpro.test.array.value_3.1.1")).isDefined shouldBe true
//
//    result.find(HoconParser.tagPath("pl.onewebpro.test.array.value_3.5")).isDefined shouldBe false
//  }
//
//  it should "return correctly values from object.conf file" in {
//    val config: Config = loadConfig("object.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//
//    result.find(HoconParser.tagPath("pl.onewebpro.test.object.reference")).isDefined shouldBe true
//    result.find(HoconParser.tagPath("pl.onewebpro.test.object.value_1")).isDefined shouldBe true
//    result.find(HoconParser.tagPath("pl.onewebpro.test.object.value_1.0.value_1_1_1")).isDefined shouldBe true
//    result.find(HoconParser.tagPath("pl.onewebpro.test.object.value_1.4.value_1_5_1")).isDefined shouldBe true
//    result.find(HoconParser.tagPath("pl.onewebpro.test.object.value_1.4.value_1_5_1.0")).isDefined shouldBe true
//
//    result.find(HoconParser.tagPath("pl.onewebpro.test.object.value_1.4.value_1_5_1.10")).isDefined shouldBe false
//  }
//
//  behavior of "HoconResult.onlyWithEnvironments"
//
//  it should "extract proper env hocon values from simple.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("simple.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyWithEnvironments
//
//    extractonResult.size shouldBe 0
//  }
//
//  it should "extract proper env hocon values from reference.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("reference.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyWithEnvironments
//
//    extractonResult.size shouldBe 2
//  }
//
//  it should "extract proper env hocon values from concatenation.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("concatenation.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyWithEnvironments
//
//    extractonResult.size shouldBe 9
//  }
//
//  it should "extract proper env hocon values from merge.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("merge.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyWithEnvironments
//
//    extractonResult.size shouldBe 3
//  }
//
//  it should "extract proper env hocon values from array.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("array.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyWithEnvironments
//
//    extractonResult.size shouldBe 4
//  }
//
//  it should "extract proper env hocon values from object.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("object.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyWithEnvironments
//
//    extractonResult.size shouldBe 5
//  }
//
//  behavior of "HoconResult.onlyEnvironments"
//
//  it should "extract proper env hocon values from simple.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("simple.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyEnvironments
//
//    extractonResult.size shouldBe 0
//  }
//
//  it should "extract proper env hocon values from reference.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("reference.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyEnvironments
//
//    extractonResult.size shouldBe 2
//  }
//
//  it should "extract proper env hocon values from concatenation.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("concatenation.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyEnvironments
//
//    extractonResult.size shouldBe 2
//  }
//
//  it should "extract proper env hocon values from merge.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("merge.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyEnvironments
//
//    extractonResult.size shouldBe 2
//  }
//
//  it should "extract proper env hocon values from array.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("array.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyEnvironments
//
//    extractonResult.size shouldBe 2
//  }
//
//  it should "extract proper env hocon values from object.conf" in {
//
//    import pl.onewebpro.hocon.utils.parser.transformers.HoconResultTransformers._
//
//    val config: Config = loadConfig("object.conf")
//    val result: HoconResult = HoconParser(config).unsafeRunSync()
//    val extractonResult = result.onlyEnvironments
//
//    extractonResult.size shouldBe 2
//  }
}
