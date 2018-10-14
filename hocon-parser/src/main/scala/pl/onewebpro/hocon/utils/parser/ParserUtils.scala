package pl.onewebpro.hocon.utils.parser

import com.typesafe.config.ConfigValue
import pl.onewebpro.hocon.utils.parser.HoconParser.{CanonicalClassName, tagCanonicalName}

private[parser] object ParserUtils {

  implicit class ConviValueImplicits(value: ConfigValue) {
    val canonicalName: CanonicalClassName = tagCanonicalName(value.getClass.getCanonicalName)
  }

}
