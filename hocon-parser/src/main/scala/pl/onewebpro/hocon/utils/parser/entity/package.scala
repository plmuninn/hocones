package pl.onewebpro.hocon.utils.parser

import pl.onewebpro.hocon.utils.parser.HoconParser.Path

package object entity {

  trait Result

  trait HoconResultValue extends Result {
    def path: Path
  }

  trait HoconResultType extends HoconResultValue {
    def values: Iterable[Result]
  }

  trait HoconValueType extends HoconResultValue

  trait HoconSimpleValueType extends HoconResultValue

}
