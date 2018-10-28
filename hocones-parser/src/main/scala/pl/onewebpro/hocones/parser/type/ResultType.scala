package pl.onewebpro.hocones.parser.`type`

object ResultType extends Enumeration {
  type ResultType = Value

  val LIST = Value("com.typesafe.config.impl.SimpleConfigList")
  val OBJECT = Value("com.typesafe.config.impl.SimpleConfigObject")
}
