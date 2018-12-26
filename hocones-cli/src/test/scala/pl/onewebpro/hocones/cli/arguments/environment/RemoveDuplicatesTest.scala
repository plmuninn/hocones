package pl.onewebpro.hocones.cli.arguments.environment

import pl.onewebpro.hocones.cli.arguments.ArgumentSpec

class RemoveDuplicatesTest extends ArgumentSpec {

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
