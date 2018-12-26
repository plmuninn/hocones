package pl.onewebpro.hocones.cli.commands

import pl.onewebpro.hocones.cli.CliSpec
import pl.onewebpro.hocones.cli.commands.Environment.EnvironmentCommand

class EnvironmentTest extends CliSpec {
  "Environment.cmd" should "return proper command for output file and alignment arguments" in withTestFile(
    "./env-test-file",
    "{}") {
    withTestFile("./env-test-output-file", "") {
      testOpts(Environment.cmd, "env-file -r -c -d -o ./env-test-output-file ./env-test-file") { result =>
        result.isRight shouldBe true

        result.right.get.input.getPath shouldBe "./env-test-file"
        result.right.get match {
          case cmd: EnvironmentCommand =>
            cmd.output.isDefined shouldBe true
            cmd.output.get.getPath shouldBe "./env-test-output-file"
            cmd.removeDuplicates shouldBe true
            cmd.withComments shouldBe true
            cmd.withDefaults shouldBe true
          case _ => fail("Wrong type of returned command.")
        }
      }
    }
  }

  "Environment.cmd" should "return proper command for default arguments" in withTestFile("./env-test-file", "{}") {
    testOpts(Environment.cmd, "env-file ./env-test-file") { result =>
      result.isRight shouldBe true

      result.right.get.input.getPath shouldBe "./env-test-file"
      result.right.get match {
        case cmd: EnvironmentCommand =>
          cmd.output.isEmpty shouldBe true
          cmd.removeDuplicates shouldBe false
          cmd.withDefaults shouldBe false
          cmd.withComments shouldBe false
        case _ => fail("Wrong type of returned command.")
      }
    }
  }
}
