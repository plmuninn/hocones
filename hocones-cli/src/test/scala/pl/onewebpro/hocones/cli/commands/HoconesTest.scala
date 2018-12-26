package pl.onewebpro.hocones.cli.commands

import pl.onewebpro.hocones.cli.CliSpec

class HoconesTest extends CliSpec {
  "Hocones.cmd" should "load properly input file" in withTestFile("./hocones-test-file", "{}") {
    testCmd(Hocones.cmd, "./hocones-test-file") { result =>
      result.isRight shouldBe true

      result.right.get.input.getPath shouldBe "./hocones-test-file"
    }
  }

  "Hocones.cmd" should "fail if there is not input file" in withTestFile("./hocones-test-file", "{}") {
    testCmd(Hocones.cmd, "") { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head shouldBe "Missing expected command (statistics or env-file or env-docs or docs), or positional argument!"
    }
  }

}
