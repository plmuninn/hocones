package pl.onewebpro.hocon.utils.parser

import cats.effect.IO
import pl.onewebpro.hocon.utils.parser.entity.Result

private[parser] object HoconReferenceResolver {


  //TODO me
  def apply(values: Iterable[Result]): IO[Iterable[Result]] = {

    IO.pure(values)
  }
}
