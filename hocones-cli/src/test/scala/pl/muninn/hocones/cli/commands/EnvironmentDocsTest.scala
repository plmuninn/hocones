package pl.muninn.hocones.cli.commands

import pl.muninn.hocones.cli.CliSpec
import pl.muninn.hocones.cli.commands.EnvironmentDocs.EnvironmentDocsCommand

class EnvironmentDocsTest extends CliSpec {

  "EnvironmentDocs.cmd" should "return proper command for output file and alignment arguments" in withTestFile(
    "./env-docs-test-file",
    "{}"
  ) {
    withTestFile("./env-docs-test-output-file", "") {
      testOpts(EnvironmentDocs.cmd, "env-docs -o ./env-docs-test-output-file ./env-docs-test-file") { result =>
        result.isRight shouldBe true

        result.right.get.input.getPath shouldBe "./env-docs-test-file"
        result.right.get match {
          case cmd: EnvironmentDocsCommand =>
            cmd.output.isDefined shouldBe true
            cmd.output.get.getPath shouldBe "./env-docs-test-output-file"
          case _ => fail("Wrong type of returned command.")
        }
      }
    }
  }

  "EnvironmentDocs.cmd" should "return proper command for default arguments" in withTestFile(
    "./env-docs-test-file",
    "{}"
  ) {
    testOpts(EnvironmentDocs.cmd, "env-docs ./env-docs-test-file") { result =>
      result.isRight shouldBe true

      result.right.get.input.getPath shouldBe "./env-docs-test-file"
      result.right.get match {
        case cmd: EnvironmentDocsCommand =>
          cmd.output.isEmpty shouldBe true
        case _ => fail("Wrong type of returned command.")
      }
    }
  }
}
