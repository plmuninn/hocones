package pl.onewebpro.hocon.utils.env.model

case class EnvironmentValue(name: String, defaultValue: Option[String], comments: Iterable[String])
