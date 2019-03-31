package pl.muninn.hocones.cli.commands

import pl.muninn.hocones.cli.CliSpec
import pl.muninn.hocones.cli.commands.Hocones.HoconesCommand
import pl.muninn.hocones.md.config.Configuration.TableAlignment

class HoconesTest extends CliSpec {
  "Hocones.cmd" should "load properly input file" in withTestFile("./hocones-test-file", "{}") {
    testCmd(Hocones.cmd, "./hocones-test-file") { result =>
      result match {
        case Right(command: HoconesCommand) =>
          command.alignment.isDefined shouldBe true
          command.alignment.get shouldBe TableAlignment.Left

          command.removeDuplicates.isDefined shouldBe true
          command.removeDuplicates.get shouldBe false

          command.withComments.isDefined shouldBe true
          command.withComments.get shouldBe false

          command.withDefaults.isDefined shouldBe true
          command.withDefaults.get shouldBe false
        case _ => fail("We should get proper result")
      }

      result.right.get.input.getPath shouldBe "./hocones-test-file"
    }
  }

  "Hocones.cmd" should "load properly input file with all arguments" in withTestFile("./hocones-test-file", "{}") {
    testCmd(Hocones.cmd, "./hocones-test-file -a right -c -d -r") { result =>
      result match {
        case Right(command: HoconesCommand) =>
          command.alignment.isDefined shouldBe true
          command.alignment.get shouldBe TableAlignment.Right

          command.removeDuplicates.isDefined shouldBe true
          command.removeDuplicates.get shouldBe true

          command.withComments.isDefined shouldBe true
          command.withComments.get shouldBe true

          command.withDefaults.isDefined shouldBe true
          command.withDefaults.get shouldBe true
        case _ => fail("We should get proper result")
      }

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
