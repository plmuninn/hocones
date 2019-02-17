package pl.onewebpro.hocones.env.model

import pl.onewebpro.hocones.common.DefaultValue.DefaultValue
import pl.onewebpro.hocones.env.model.comment.Comment

case class EnvironmentValue(name: Name, defaultValue: Option[DefaultValue], comments: Iterable[Comment])
