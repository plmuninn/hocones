package pl.muninn.hocones.cli

import java.io.{File, PrintWriter}

import com.monovore.decline.{Command, Help, Opts}
import org.scalatest.{Assertion, FlatSpec, Matchers}

trait CliSpec extends FlatSpec with Matchers {

  def testOpts[T](opts: Opts[T], value: String)(fn: Either[Help, T] => Assertion): Assertion =
    testCmd[T](Command(name = "test", header = "")(opts), value)(fn)

  def testCmd[T](cmd: Command[T], value: String)(fn: Either[Help, T] => Assertion): Assertion = {
    val arguments = value.split(" ").filterNot(_.isEmpty).toList
    val parsedResult = cmd.parse(arguments)
    fn(parsedResult)
  }

  def testOpts[T](opts: Opts[T])(fn: Either[Help, T] => Assertion): Assertion = testOpts[T](opts, "")(fn)

  def withTestFile(fileName: String, source: String)(fn: => Assertion): Assertion = {
    val testFile = new File(fileName)
    val writer = new PrintWriter(testFile)
    writer.write(source)
    writer.close()
    try {
      val result: Assertion = fn
      result
    } finally {
      testFile.delete()
    }
  }

}
