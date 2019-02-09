package pl.onewebpro.hocones.parser
import com.typesafe.config.ConfigValue
import org.scalamock.scalatest.MockFactory
import pl.onewebpro.hocones.common.implicits._
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{ComposedConfigValue, NotResolvedRef, ResolvedRef, SimpleValue}

class HoconReferenceResolverTest extends TestSpec with MockFactory {

  behavior of "HoconReferenceResolver.resolveSimpleValue"

  it should "resolve ComposedConfigValue" in new Context {
    val composed = ComposedConfigValue("""${pl.test.sample.value}"/test"""").unsafeRunSync()
    val simpleValue = SimpleValue(""""/test"""")
    val result = HoconReferenceResolver.resolveSimpleValue(resultList, composed)
    result.asInstanceOf[ComposedConfigValue].values shouldBe List(resolvedRef, simpleValue)
  }

  behavior of "HoconReferenceResolver.resolveResult"

  it should "resolve NotResolvedRef" in new Context {
    val result = HoconReferenceResolver.resolveResult(resultList, notResolvedRef)
    result shouldBe resolvedRef
  }

  it should "resolve ComposedConfigValue" in new Context {
    val composed = ComposedConfigValue("""${pl.test.sample.value}"/test"""").unsafeRunSync()
    val simpleValue = SimpleValue(""""/test"""")
    val result = HoconReferenceResolver.resolveResult(resultList, composed)
    result.asInstanceOf[ComposedConfigValue].values shouldBe List(resolvedRef, simpleValue)
  }

  it should "resolve HoconObject" in new Context {
    val hrv = HoconReferenceValue("pl.test.config.value1", cfg, notResolvedRef)
    val hoconObject = HoconObject("pl.test.config", cfg, Seq(hrv))
    val expected = HoconObject("pl.test.config", cfg, Seq(HoconResolvedReference(hoconValue, hrv)))
    val result = HoconReferenceResolver.resolveResult(resultList, hoconObject)
    result shouldBe expected
  }

  it should "resolve HoconMergedValues" in new Context {
    val default = hoconValue.copy(path = "pl.test.config.value1")
    val replaced = HoconReferenceValue("pl.test.config.value1", cfg, notResolvedRef)
    val merged = HoconMergedValues("pl.test.merged", cfg, default, replaced)
    val expected = HoconMergedValues("pl.test.merged", cfg, default, HoconResolvedReference(hoconValue, replaced))
    val result = HoconReferenceResolver.resolveResult(resultList, merged)
    result shouldBe expected
  }

  trait Context {
    val cfg: ConfigValue = mock[ConfigValue]
    val notResolvedRef: NotResolvedRef = NotResolvedRef("${pl.test.sample.value}").unsafeRunSync()
    val hoconValue = HoconValue("pl.test.sample.value", cfg, SimpleValueType.INT, SimpleValue("1"))
    val resolvedRef: ResolvedRef = ResolvedRef(hoconValue, notResolvedRef)
    val resultList: Map[Path, HoconResultValue] =
      Map[Path, HoconResultValue](tagPath("pl.test.sample.value") -> hoconValue)
  }

}
