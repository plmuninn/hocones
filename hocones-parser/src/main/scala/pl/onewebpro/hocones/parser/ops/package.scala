package pl.onewebpro.hocones.parser

import pl.onewebpro.hocones.parser.entity.simple.{
  EnvironmentValue,
  NotResolvedRef,
  ResolvedRef
}

package object ops {
  private[ops] implicit val extractEnvironmentValue
    : ExtractHoconValue[EnvironmentValue] =
    new ExtractHoconValue[EnvironmentValue]({
      case _: EnvironmentValue => true
      case _                   => false
    })

  private[ops] implicit val extractNotResolvedRef
    : ExtractHoconValue[NotResolvedRef] =
    new ExtractHoconValue[NotResolvedRef]({
      case _: NotResolvedRef => true
      case _                 => false
    })

  private[ops] implicit val extractResolvedRef: ExtractHoconValue[ResolvedRef] =
    new ExtractHoconValue[ResolvedRef]({
      case _: ResolvedRef => true
      case _              => false
    })
}
