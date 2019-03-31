package pl.muninn.hocones.env.model

import cats.implicits._
import cats.Show
import pl.muninn.hocones.common.DefaultValue.DefaultValue
import pl.muninn.hocones.env.model.comment.MetaInformationComments.MetaComment
import pl.muninn.hocones.env.model.comment.{Comment, FileName, IsOptional, Path}

package object show {

  private def prefixComment[T <: Comment](value: T)(implicit s: Show[T]): String = "# " + s.show(value)

  implicit val showBoolean: Show[Boolean] = Show(value => if (value) "yes" else "no")

  implicit val showPath: Show[Path] = Show(path => s"Path: ${path.value}")

  implicit val showFileName: Show[FileName] = Show(fileName => s"File name: ${fileName.value}")

  implicit val showIsOptional: Show[IsOptional] = Show(
    isOptional => s"Optional: ${showBoolean.show(isOptional.value)}"
  )

  implicit val showName: Show[Name] = Show(name => s"$name=")

  implicit val showDefaultValue: Show[Option[DefaultValue]] = Show(value => value.map(_.toString).getOrElse(""))

  implicit val showComment: Show[Comment] = Show {
    case comment: MetaComment   => prefixComment(comment)
    case path: Path             => prefixComment(path)
    case fileName: FileName     => prefixComment(fileName)
    case isOptional: IsOptional => prefixComment(isOptional)
  }

  implicit val showEnvironmentValue: Show[EnvironmentValue] = Show { value =>
    val comments = value.comments.map(_.show).mkString("\n")

    comments + "\n" + value.name.show + value.defaultValue.show
  }

}
