package pl.muninn.hocones.cli.commands

import pl.muninn.hocones.cli.CliSpec
import pl.muninn.hocones.cli.commands.Docs.DocsCommand

class DocsTest extends CliSpec {

  "Docs.cmd" should "return proper command for output file" in withTestFile("./docs-test-file",
                                                                                                    "{}") {
    withTestFile("./docs-test-output-file", "") {
      testOpts(Docs.cmd, "docs -o ./docs-test-output-file ./docs-test-file") { result =>
        result.isRight shouldBe true

        result.right.get.input.getPath shouldBe "./docs-test-file"
        result.right.get match {
          case cmd: DocsCommand =>
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
          cmd.output.isEmpty shouldBe true
        case _ => fail("Wrong type of returned command.")
      }
    }
  }
}
