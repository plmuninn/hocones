package pl.onewebpro.hocones.parser.entity.simple

import cats.effect.IO
import pl.onewebpro.hocones.parser.ParsingError
import pl.onewebpro.hocones.parser.entity.simple.EnvironmentValue.{EnvName, EnvValue}
import pl.onewebpro.hocones.parser.entity.simple.NotResolvedRef.{HoconRefName, HoconRefValue}
import shapeless.tag
import shapeless.tag.@@

case class NotResolvedRef(env: HoconRefValue, name: HoconRefName, isOptional: Boolean) extends ReferenceTypeValue {
  lazy val nameChunks: Iterable[String] = name.split('.')
}

object NotResolvedRef {

  private[simple] object InternalNotResolvedRef {

    trait HoconRefTag

    trait HoconRefNameTag

  }

  import InternalNotResolvedRef._

  type HoconRefValue = String @@ HoconRefTag
  type HoconRefName = String @@ HoconRefNameTag

  private def tagEnvToRef(env: EnvValue): HoconRefValue =
    tag[HoconRefTag][String](env)

  private def tagEnvNameToRefName(env: EnvName): HoconRefName =
    tag[HoconRefNameTag][String](env)

  //TODO test me
  private[simple] def envIsRef(env: EnvValue): Boolean = env.contains(".")

  private[simple] def isRef(value: String): Boolean =
    EnvironmentValue.envName(value).exists(_.contains("."))

  def apply(value: String): IO[NotResolvedRef] =
    EnvironmentValue.envName(value).filter(envIsRef) match {
      case Some(env) =>
        EnvironmentValue.extractName(env) match {
          case Some(name) =>
            IO(
              NotResolvedRef(env = tagEnvToRef(env),
                             name = tagEnvNameToRefName(name),
                             isOptional = EnvironmentValue.isOptionalEnv(env)))
          case None =>
            IO.raiseError(ParsingError(s"Error during extracting environment from string $value"))
        }
      case None =>
        IO.raiseError(ParsingError(s"Value $value is not environment"))
    }

}
