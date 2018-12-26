package pl.onewebpro.hocones.cli.commands

import pl.onewebpro.hocones.cli.CliSpec

class StatisticsTest extends CliSpec {
  "Statistics.cmd" should "load properly input file" in withTestFile("./statistics-test-file", "{}") {
    testOpts(Statistics.cmd, "statistics ./statistics-test-file") { result =>
      result.isRight shouldBe true

      result.right.get.input.getPath shouldBe "./statistics-test-file"
    }
  }

  "Statistics.cmd" should "fail if there is not input file" in withTestFile("./statistics-test-file", "{}") {
    testOpts(Statistics.cmd, "statistics") { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head shouldBe "Missing expected positional argument!"
    }
  }

}
