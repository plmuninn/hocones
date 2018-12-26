package pl.onewebpro.hocones.parser

import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef}

package object ops {
  implicit private[ops] val extractEnvironmentValue: ExtractHoconValue[EnvironmentValue] =
    new ExtractHoconValue[EnvironmentValue]({
      case _: EnvironmentValue => true
      case _                   => false
    })

  implicit private[ops] val extractNotResolvedRef: ExtractHoconValue[NotResolvedRef] =
    new ExtractHoconValue[NotResolvedRef]({
      case _: NotResolvedRef => true
      case _                 => false
    })

  implicit private[ops] val extractResolvedRef: ExtractHoconValue[ResolvedRef] =
    new ExtractHoconValue[ResolvedRef]({
      case _: ResolvedRef => true
      case _              => false
    })
}
