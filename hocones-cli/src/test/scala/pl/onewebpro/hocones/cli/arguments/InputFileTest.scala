package pl.onewebpro.hocones.cli.arguments
import java.io.{File, PrintWriter}

class InputFileTest extends ArgumentSpec {
  "InputFile.opts" should "return error if file not exists" in {
    testOpts(InputFile.opts, "./test-file") { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head.contains("./test-file not exists") shouldBe true
    }
  }

  "InputFile.opts" should "return error if file is not hocon" in {
    val testFile = new File("./test-file")

    val writer = new PrintWriter(testFile)
    writer.write("------")
    writer.close()

    testOpts(InputFile.opts, "./test-file") { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head.contains("./test-file is not proper hocon") shouldBe true
    }

    testFile.delete()
  }

  "InputFile.opts" should "return path to file if everything is ok" in {
    val testFile = new File("./test-file")

    val writer = new PrintWriter(testFile)
    writer.write("{}")
    writer.close()

    testOpts(InputFile.opts, "./test-file") { result =>
      result.isRight shouldBe true
      result.right.get.getPath shouldBe "./test-file"
    }

    testFile.delete()
  }

  "InputFile.opts" should "fail as default" in {
    testOpts(InputFile.opts) { result =>
      result.isLeft shouldBe true

      result.left.get.errors.head shouldBe "Missing expected positional argument!"
    }
  }
}
