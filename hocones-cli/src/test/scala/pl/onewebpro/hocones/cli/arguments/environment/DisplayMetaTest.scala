package pl.onewebpro.hocones.cli.arguments.environment

import pl.onewebpro.hocones.cli.CliSpec

class DisplayMetaTest extends CliSpec {

  "DisplayMeta.opts" should "parse flag properly" in {
    testOpts(DisplayMeta.opts, "--meta-information") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true

    }

    testOpts(DisplayMeta.opts, "-m") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true
    }
  }

  "DisplayMeta.opts" should "return proper default value" in {
    testOpts(DisplayMeta.opts) { result =>
      result.isRight shouldBe true
      result.right.get shouldBe false
    }
  }

}
