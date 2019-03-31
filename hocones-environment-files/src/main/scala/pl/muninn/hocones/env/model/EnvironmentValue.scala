package pl.muninn.hocones.env.model

import pl.muninn.hocones.common.DefaultValue.DefaultValue
import pl.muninn.hocones.env.model.comment.Comment

case class EnvironmentValue(name: Name, defaultValue: Option[DefaultValue], comments: Iterable[Comment])
