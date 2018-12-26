package pl.onewebpro.hocones.cli.arguments
import com.monovore.decline.{Command, Help, Opts}
import org.scalatest.{Assertion, FlatSpec, Matchers}

trait ArgumentSpec extends FlatSpec with Matchers {

  def testOpts[T](opts: Opts[T], value: String)(fn: Either[Help, T] => Assertion): Assertion = {
    val arguments = value.split(" ").filterNot(_.isEmpty).toList
    val parsedResult = Command(name = "test", header = "")(opts).parse(arguments)
    fn(parsedResult)
  }

  def testOpts[T](opts: Opts[T])(fn: Either[Help, T] => Assertion): Assertion = testOpts[T](opts, "")(fn)

}
