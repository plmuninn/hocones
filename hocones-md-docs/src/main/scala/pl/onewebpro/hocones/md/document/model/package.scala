package pl.onewebpro.hocones.md.document

import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.meta.model.MetaValue
import pl.onewebpro.hocones.parser.entity._

package object model {

  case class Documentation(roots: Map[String, Seq[Document[_]]], orphans: Seq[Document[_]])

  trait Document[T <: HoconResultValue] {
    def path: Path

    def metaInformation: MetaValue

    def value: T
  }

  case class ArrayDocument(path: Path, metaInformation: MetaValue, value: HoconArray) extends Document[HoconArray]

  case class ConcatenationDocument(path: Path, metaInformation: MetaValue, value: HoconConcatenation)
      extends Document[HoconConcatenation]

  case class EnvironmentDocument(path: Path, metaInformation: MetaValue, value: HoconEnvironmentValue)
      extends Document[HoconEnvironmentValue]

  case class MergedValuesDocument(path: Path, metaInformation: MetaValue, value: HoconMergedValues)
      extends Document[HoconMergedValues]

  case class ObjectDocument(path: Path, metaInformation: MetaValue, value: HoconObject) extends Document[HoconObject]

  case class ReferenceValueDocument(path: Path, metaInformation: MetaValue, value: HoconReferenceValue)
      extends Document[HoconReferenceValue]

  case class ResolvedReferenceDocument(path: Path, metaInformation: MetaValue, value: HoconResolvedReference)
      extends Document[HoconResolvedReference]

  case class ValueDocument(path: Path, metaInformation: MetaValue, value: HoconValue) extends Document[HoconValue]

}
