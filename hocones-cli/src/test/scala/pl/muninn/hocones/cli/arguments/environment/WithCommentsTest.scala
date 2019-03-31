package pl.muninn.hocones.cli.arguments.environment

import pl.muninn.hocones.cli.CliSpec

class WithCommentsTest extends CliSpec {

  "WithComments.opts" should "parse flag properly" in {
    testOpts(WithComments.opts, "--comments") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true

    }

    testOpts(WithComments.opts, "-c") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true
    }
  }

  "WithComments.opts" should "return proper default value" in {
    testOpts(WithComments.opts) { result =>
      result.isRight shouldBe true
      result.right.get shouldBe false
    }
  }
}
