package pl.onewebpro.hocones.cli.arguments.environment

import pl.onewebpro.hocones.cli.CliSpec

class WithDefaultsTest extends CliSpec {

  "WithDefaults.opts" should "parse flag properly" in {
    testOpts(WithDefaults.opts, "--defaults") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true

    }

    testOpts(WithDefaults.opts, "-d") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true
    }
  }

  "WithDefaults.opts" should "return proper default value" in {
    testOpts(WithDefaults.opts) { result =>
      result.isRight shouldBe true
      result.right.get shouldBe false
    }
  }

}
