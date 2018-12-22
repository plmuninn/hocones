package pl.onewebpro.hocones.parser.entity

import cats.effect.IO

package object simple {

  trait SimpleHoconValue extends Result

  trait ReferenceTypeValue extends SimpleHoconValue

  //TODO test me
  object SimpleHoconValue {
    def apply(value: String): IO[SimpleHoconValue] =
      if (NotResolvedRef.isRef(value)) NotResolvedRef(value)
      else if (ComposedConfigValue.isComposedValue(value))
        ComposedConfigValue(value)
      else if (EnvironmentValue.isEnv(value)) EnvironmentValue(value)
      else IO(SimpleValue(value))
  }

}
