package pl.onewebpro.hocones.parser.`type`

object SimpleValueType extends Enumeration {
  type SimpleValueType = Value

  val UNQUOTED_STRING = Value("com.typesafe.config.impl.ConfigString.Unquoted")
  val QUOTED_STRING = Value("com.typesafe.config.impl.ConfigString.Quoted")
  val BOOLEAN = Value("com.typesafe.config.impl.ConfigBoolean")
  val DOUBLE = Value("com.typesafe.config.impl.ConfigDouble")
  val INT = Value("com.typesafe.config.impl.ConfigInt")
  val LONG = Value("com.typesafe.config.impl.ConfigLong")
  val NULL = Value("com.typesafe.config.impl.ConfigNull")
}
