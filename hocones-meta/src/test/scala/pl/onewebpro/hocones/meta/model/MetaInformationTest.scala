package pl.onewebpro.hocones.meta.model

import org.scalatest.{FlatSpec, Matchers}

class MetaInformationTest extends FlatSpec with Matchers {

  val testStructure = MetaInformation(
    hoconesVersion = "someVersion",
    roots = Map(
      "pl.onewebpro.hocones.meta" -> Map(
        "model" -> Seq(
          MetaString("meta-string", None, None, None, None),
          MetaNumber("meta-number", None, None, None),
          MetaGenericInformation("meta-boolean", None),
          MetaList("meta-list", None, None, None),
          MetaObject("meta-object", None, None),
        ),
        "test" -> Seq(
          MetaString("meta-string", None, None, None, None),
          MetaNumber("meta-number", None, None, None),
          MetaGenericInformation("meta-boolean", None),
          MetaList("meta-list", None, None, None),
          MetaObject("meta-object", None, None),
        )
      ),
      "pl.onewebpro.test" -> Map(
        "model" -> Seq(
          MetaString("meta-string", None, None, None, None),
          MetaNumber("meta-number", None, None, None),
          MetaGenericInformation("meta-boolean", None),
          MetaList("meta-list", None, None, None),
          MetaObject("meta-object", None, None),
        )
      )
    ),
    orphans = Seq(
      MetaString("orphans-meta-string", None, None, None, None),
      MetaNumber("orphans-meta-number", None, None, None),
      MetaGenericInformation("orphans-meta-boolean", None),
      MetaList("orphans-meta-list", None, None, None),
      MetaObject("orphans-meta-object", None, None),
    )
  )

  "MetaInformation.findByName" should "return proper values using names" in {
    testStructure.findByName("orphans-meta-string").isDefined shouldBe true
    testStructure.findByName("orphans-meta-number").isDefined shouldBe true
    testStructure.findByName("orphans-meta-boolean").isDefined shouldBe true
    testStructure.findByName("orphans-meta-list").isDefined shouldBe true
    testStructure.findByName("orphans-meta-object").isDefined shouldBe true

    testStructure.findByName("meta-string").isDefined shouldBe true
    testStructure.findByName("meta-number").isDefined shouldBe true
    testStructure.findByName("meta-boolean").isDefined shouldBe true
    testStructure.findByName("meta-list").isDefined shouldBe true
    testStructure.findByName("meta-object").isDefined shouldBe true

  }

  "MetaInformation.findByPath" should "return proper values using path" in {
    testStructure.findByPath("pl.onewebpro.hocones.meta.model").size shouldBe 5
    testStructure.findByPath("pl.onewebpro.hocones.meta.test").size shouldBe 5
    testStructure.findByPath("pl.onewebpro.test.model").size shouldBe 5
  }

  "MetaInformation.findByPathAndName" should "return proper values using path and name" in {
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model", "meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model", "meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model", "meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model", "meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model", "meta-object").isDefined shouldBe true

    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test", "meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test", "meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test", "meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test", "meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test", "meta-object").isDefined shouldBe true

    testStructure.findByPathAndName("pl.onewebpro.test.model", "meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model", "meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model", "meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model", "meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model", "meta-object").isDefined shouldBe true
  }

  "MetaInformation.findByPathAndName" should "return proper values using path and name connected" in {
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model.meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model.meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model.meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model.meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.model.meta-object").isDefined shouldBe true

    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test.meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test.meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test.meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test.meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.hocones.meta.test.meta-object").isDefined shouldBe true

    testStructure.findByPathAndName("pl.onewebpro.test.model.meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model.meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model.meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model.meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("pl.onewebpro.test.model.meta-object").isDefined shouldBe true

    testStructure.findByPathAndName("orphans-meta-string").isDefined shouldBe true
    testStructure.findByPathAndName("orphans-meta-number").isDefined shouldBe true
    testStructure.findByPathAndName("orphans-meta-boolean").isDefined shouldBe true
    testStructure.findByPathAndName("orphans-meta-list").isDefined shouldBe true
    testStructure.findByPathAndName("orphans-meta-object").isDefined shouldBe true
  }

}
