package pl.onewebpro.hocones.parser

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocones.common.implicits.Path

package object entity {

  trait Result

  trait HoconResultValue extends Result {
    def path: Path

    def cfg: ConfigValue
  }

  trait HoconResultType extends HoconResultValue {
    def values: Seq[HoconResultValue]
  }

  trait HoconValueType extends HoconResultValue

  trait HoconSimpleValueType extends HoconResultValue

}
