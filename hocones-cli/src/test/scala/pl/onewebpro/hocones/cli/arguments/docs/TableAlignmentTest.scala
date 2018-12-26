package pl.onewebpro.hocones.cli.arguments.docs

import com.monovore.decline.Help
import org.scalatest.Assertion
import pl.onewebpro.hocones.cli.arguments.ArgumentSpec
import pl.onewebpro.hocones.md.config.Configuration.{TableAlignment => MdTableAlignment}

class TableAlignmentTest extends ArgumentSpec {

  val testAlignment: MdTableAlignment.TableAlignment => Either[Help, MdTableAlignment.TableAlignment] => Assertion = {
    alignment =>
      { result =>
        result.isRight shouldBe true
        result.right.get shouldBe alignment
      }
  }

  "TableAligment.opts" should "parse left, right, center properly" in {

    testOpts(TableAlignment.opts, "-a left")(testAlignment(MdTableAlignment.Left))
    testOpts(TableAlignment.opts, "--alignment left")(testAlignment(MdTableAlignment.Left))

    testOpts(TableAlignment.opts, "-a right")(testAlignment(MdTableAlignment.Right))
    testOpts(TableAlignment.opts, "--alignment right")(testAlignment(MdTableAlignment.Right))

    testOpts(TableAlignment.opts, "-a center")(testAlignment(MdTableAlignment.Center))
    testOpts(TableAlignment.opts, "--alignment center")(testAlignment(MdTableAlignment.Center))
  }

  "TableAligment.opts" should "return proper default value" in {
    testOpts(TableAlignment.opts)(testAlignment(MdTableAlignment.Left))
  }

}
