package pl.muninn.hocones.cli.commands

import pl.muninn.hocones.cli.CliSpec
import pl.muninn.hocones.cli.commands.Docs.DocsCommand
import pl.muninn.hocones.md.config.Configuration.{TableAlignment => MdTableAlignment}

class DocsTest extends CliSpec {

  "Docs.cmd" should "return proper command for output file and alignment arguments" in withTestFile("./docs-test-file",
                                                                                                    "{}") {
    withTestFile("./docs-test-output-file", "") {
      testOpts(Docs.cmd, "docs -o ./docs-test-output-file -a right ./docs-test-file") { result =>
        result.isRight shouldBe true

        result.right.get.input.getPath shouldBe "./docs-test-file"
        result.right.get match {
          case cmd: DocsCommand =>
            cmd.alignment shouldBe MdTableAlignment.Right
            cmd.output.isDefined shouldBe true
            cmd.output.get.getPath shouldBe "./docs-test-output-file"
          case _ => fail("Wrong type of returned command.")
        }
      }
    }
  }

  "Docs.cmd" should "return proper command for default arguments" in withTestFile("./docs-test-file", "{}") {
    testOpts(Docs.cmd, "docs ./docs-test-file") { result =>
      result.isRight shouldBe true

      result.right.get.input.getPath shouldBe "./docs-test-file"
      result.right.get match {
        case cmd: DocsCommand =>
          cmd.alignment shouldBe MdTableAlignment.Left
          cmd.output.isEmpty shouldBe true
        case _ => fail("Wrong type of returned command.")
      }
    }
  }
}
