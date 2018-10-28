package pl.onewebpro.hocones.env.model

case class EnvironmentValue(name: String, defaultValue: Option[String], comments: Iterable[String])
