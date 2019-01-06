package pl.onewebpro.hocones.env.model
import pl.onewebpro.hocones.env.TestSpec
import pl.onewebpro.hocones.env.config.Configuration.EnvironmentConfiguration
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.{HoconParser, HoconResult}

class ModelGeneratorTest extends TestSpec {

  "ModelGenerator.removeDuplicates" should "remove all duplicates" in {
    val valuesWithDuplicate = Iterable(
      EnvironmentValue(
        name = tagName("Value1"),
        defaultValue = None,
        comments = Nil
      ),
      EnvironmentValue(
        name = tagName("Value2"),
        defaultValue = None,
        comments = Nil
      ),
      EnvironmentValue(
        name = tagName("Value1"),
        defaultValue = None,
        comments = Nil
      ),
      EnvironmentValue(
        name = tagName("Value2"),
        defaultValue = Some(tagDefaultValue("Some value")),
        comments = Nil
      ),
    )

    (ModelGenerator.removeDuplicates(valuesWithDuplicate) should contain).allOf(
      EnvironmentValue(
        name = tagName("Value1"),
        defaultValue = None,
        comments = Nil
      ),
      EnvironmentValue(
        name = tagName("Value2"),
        defaultValue = Some(tagDefaultValue("Some value")),
        comments = Nil
      )
    )
  }

  "ModelGenerator.orderValues" should "order values properly" in {
    val values = Iterable(
      EnvironmentValue(
        name = tagName("C"),
        defaultValue = None,
        comments = Nil
      ),
      EnvironmentValue(
        name = tagName("A"),
        defaultValue = None,
        comments = Nil
      ),
      EnvironmentValue(
        name = tagName("B"),
        defaultValue = None,
        comments = Nil
      ),
    )

    ModelGenerator.orderValues(values).head.name shouldBe "A"
    ModelGenerator.orderValues(values).tail.head.name shouldBe "B"
    ModelGenerator.orderValues(values).last.name shouldBe "C"
  }

  "ModelGenerator.generate" should "generate proper list ov environments" in {
    val config = loadConfig("test.conf")
    val result: HoconResult = HoconParser(config).unsafeRunSync()
    val meta = MetaInformation(hoconesVersion = "1", roots = Map.empty, orphans = Nil)

    val configuration = EnvironmentConfiguration(
      outputPath = null,
      withDefaults = true,
      withComments = true,
      removeDuplicates = true
    )

    ModelGenerator.generate(configuration, result, meta).size shouldBe 7
    ModelGenerator.generate(configuration.copy(removeDuplicates = false), result, meta).size shouldBe 8
  }
}
