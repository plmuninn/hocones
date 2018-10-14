package pl.onewebpro.hocon.utils.parser.entity.`type`

object ValueType extends Enumeration {
  type ValueType = Value

  val CONCATENATION = Value("com.typesafe.config.impl.ConfigConcatenation")
  val MERGE = Value("com.typesafe.config.impl.ConfigDelayedMerge")
  val REFERENCE = Value("com.typesafe.config.impl.ConfigReference")
}
