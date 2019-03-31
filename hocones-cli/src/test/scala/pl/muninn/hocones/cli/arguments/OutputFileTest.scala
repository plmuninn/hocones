package pl.muninn.hocones.cli.arguments

import pl.muninn.hocones.cli.CliSpec

class OutputFileTest extends CliSpec {
  "OutputFile.opts" should "return error if parent directory not exists" in {
    testOpts(OutputFile.opts(""), "-o /yxz/test.file") { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head.contains("Output path /yxz/test.file is unavailable") shouldBe true
    }
  }

  "OutputFile.opts" should "return path to file if everything is ok" in withTestFile("./output-test-file", "") {
    testOpts(OutputFile.opts(""), "-o ./output-test-file") { result =>
      result.isRight shouldBe true
      result.right.get.getPath shouldBe "./output-test-file"
    }
  }

  "OutputFile.opts" should "fail as default" in {
    testOpts(OutputFile.opts("")) { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head shouldBe "Missing expected flag --output!"
    }
  }
}
