package pl.muninn.hocones.cli.arguments.environment

import pl.muninn.hocones.cli.CliSpec

class RemoveDuplicatesTest extends CliSpec {

  "RemoveDuplicates.opts" should "parse flag properly" in {
    testOpts(RemoveDuplicates.opts, "--remove-duplicates") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true

    }

    testOpts(RemoveDuplicates.opts, "-r") { result =>
      result.isRight shouldBe true
      result.right.get shouldBe true
    }
  }

  "RemoveDuplicates.opts" should "return proper default value" in {
    testOpts(RemoveDuplicates.opts) { result =>
      result.isRight shouldBe true
      result.right.get shouldBe false
    }
  }

}
