package pl.onewebpro.hocones.cli.commands

import pl.onewebpro.hocones.cli.CliSpec
import pl.onewebpro.hocones.cli.commands.EnvironmentDocs.EnvironmentDocsCommand
import pl.onewebpro.hocones.md.config.Configuration.{TableAlignment => MdTableAlignment}

class EnvironmentDocsTest extends CliSpec {

  "EnvironmentDocs.cmd" should "return proper command for output file and alignment arguments" in withTestFile(
    "./env-docs-test-file",
    "{}") {
    withTestFile("./env-docs-test-output-file", "") {
      testOpts(EnvironmentDocs.cmd, "env-docs -o ./env-docs-test-output-file -a right ./env-docs-test-file") { result =>
        result.isRight shouldBe true

        result.right.get.input.getPath shouldBe "./env-docs-test-file"
        result.right.get match {
          case cmd: EnvironmentDocsCommand =>
            cmd.alignment shouldBe MdTableAlignment.Right
            cmd.output.isDefined shouldBe true
            cmd.output.get.getPath shouldBe "./env-docs-test-output-file"
          case _ => fail("Wrong type of returned command.")
        }
      }
    }
  }

  "EnvironmentDocs.cmd" should "return proper command for default arguments" in withTestFile("./env-docs-test-file",
                                                                                             "{}") {
    testOpts(EnvironmentDocs.cmd, "env-docs ./env-docs-test-file") { result =>
      result.isRight shouldBe true

      result.right.get.input.getPath shouldBe "./env-docs-test-file"
      result.right.get match {
        case cmd: EnvironmentDocsCommand =>
          cmd.alignment shouldBe MdTableAlignment.Left
          cmd.output.isEmpty shouldBe true
        case _ => fail("Wrong type of returned command.")
      }
    }
  }
}
