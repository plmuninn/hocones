package pl.onewebpro.hocones.cli.arguments

import java.io.File

class OutputFileTest extends ArgumentSpec {
  "OutputFile.opts" should "return error if parent directory not exists" in {
    testOpts(OutputFile.opts(""), "-o /yxz/test.file") { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head.contains("Output path /yxz/test.file is unavailable") shouldBe true
    }
  }

  "OutputFile.opts" should "return path to file if everything is ok" in {
    val testFile = new File("./output-test-file")
    testFile.createNewFile()

    testOpts(OutputFile.opts(""), "-o ./output-test-file") { result =>
      result.isRight shouldBe true
      result.right.get.getPath shouldBe "./output-test-file"
    }

    testFile.delete()
  }

  "OutputFile.opts" should "fail as default" in {
    testOpts(OutputFile.opts("")) { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head shouldBe "Missing expected flag --output!"
    }
  }
}
