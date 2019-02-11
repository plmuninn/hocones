package pl.onewebpro.hocones.meta.document

import pl.onewebpro.hocones.common.DefaultValue.DefaultValue
import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.meta.document.model.DocumentType.DocumentType
import pl.onewebpro.hocones.meta.model.MetaValue
import pl.onewebpro.hocones.parser.`type`.SimpleValueType
import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.ComposedConfigValue.HoconPattern
import pl.onewebpro.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef}

package object model {

  object DocumentType extends Enumeration {
    type DocumentType = Value

    val ArrayDocument: DocumentType = Value("Array")
    val ConcatenationDocument: DocumentType = Value("Concatenation")
    val EnvironmentDocument: DocumentType = Value("Environment value")
    val MergedValuesDocument: DocumentType = Value("Merge of values")
    val ObjectDocument: DocumentType = Value("Object")
    val ReferenceValueDocument: DocumentType = Value("Unresolved reference")
    val ResolvedReferenceDocument: DocumentType = Value("Reference")
    val ValueDocument: DocumentType = Value("Array")
  }

  case class Documentation(roots: Map[String, Seq[Document[_]]], orphans: Seq[Document[_]]) {

    def findByMetaValue[T <: MetaValue](value: T): Option[Document[_]] =
      (roots.values.flatten ++ orphans).find { document =>
        document.metaInformation == value
      }
  }

  trait Document[T <: HoconResultValue] {
    def path: Path

    def metaInformation: MetaValue

    def value: T

    def name: String = metaInformation.name

    def description: Option[String] = metaInformation.description

    def packageName: String = path.packageName

    def from: Option[String] = Option(value.cfg.origin().filename())

    def typeName: DocumentType = this match {
      case _: ArrayDocument             => DocumentType.ArrayDocument
      case _: ConcatenationDocument     => DocumentType.ConcatenationDocument
      case _: EnvironmentDocument       => DocumentType.EnvironmentDocument
      case _: MergedValuesDocument      => DocumentType.MergedValuesDocument
      case _: ObjectDocument            => DocumentType.ObjectDocument
      case _: ReferenceValueDocument    => DocumentType.ReferenceValueDocument
      case _: ResolvedReferenceDocument => DocumentType.ResolvedReferenceDocument
      case _: ValueDocument             => DocumentType.ValueDocument
    }

    protected def generateDetails[A <: MetaValue](meta: A): Map[String, String] =
      meta.getClass.getDeclaredFields
        .map { field =>
          field.setAccessible(true)
          val fieldResult = Option(field.get(meta)).flatMap {
            case Some(value) => Some(value.toString)
            case None        => None
            case value       => Some(value.toString)
          }
          val result = (field.getName.replace("$minus", "-"), fieldResult)
          field.setAccessible(false)
          result
        }
        .filter {
          case (name, _) => name != "name" && name != "description"
        }
        .collect {
          case (name, Some(fieldValue)) => name -> fieldValue
        }
        .toMap

    def details: Map[String, String]
  }

  sealed trait DocumentEnvironments {
    def environments: Iterable[EnvironmentValue]
  }

  sealed trait DocumentReferences {
    def references: Iterable[ResolvedRef]
  }

  sealed trait DocumentUnresolvedReferences {
    def unresolvedReferences: Iterable[NotResolvedRef]
  }

  case class ArrayDocument(path: Path, metaInformation: MetaValue, value: HoconArray)
      extends Document[HoconArray]
      with DocumentEnvironments
      with DocumentReferences
      with DocumentUnresolvedReferences {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val size: Int = value.values.size

    val environments: Iterable[EnvironmentValue] = value.values.extract[EnvironmentValue]

    val references: Iterable[ResolvedRef] = value.values.extract[ResolvedRef]

    val unresolvedReferences: Iterable[NotResolvedRef] = value.values.extract[NotResolvedRef]

    val details: Map[String, String] = generateDetails(metaInformation)
  }

  case class ConcatenationDocument(path: Path, metaInformation: MetaValue, value: HoconConcatenation)
      extends Document[HoconConcatenation]
      with DocumentEnvironments
      with DocumentReferences
      with DocumentUnresolvedReferences {

    val pattern: HoconPattern = value.value.pattern

    val environments: Iterable[EnvironmentValue] =
      value.value.values.collect {
        case value: EnvironmentValue => value
      }

    val references: Iterable[ResolvedRef] =
      value.value.values.collect {
        case value: ResolvedRef => value
      }

    val unresolvedReferences: Iterable[NotResolvedRef] =
      value.value.values.collect {
        case value: NotResolvedRef => value
      }

    val details: Map[String, String] = generateDetails(metaInformation)
  }

  case class EnvironmentDocument(path: Path, metaInformation: MetaValue, value: HoconEnvironmentValue)
      extends Document[HoconEnvironmentValue] {

    val environmentName: String = value.value.name
    val isOptional: Boolean = value.value.isOptional

    val details: Map[String, String] = generateDetails(metaInformation)
  }

  case class MergedValuesDocument(path: Path, metaInformation: MetaValue, value: HoconMergedValues)
      extends Document[HoconMergedValues] {

    val defaultValue: Option[DefaultValue] = value.extractDefaultValue

    val details: Map[String, String] = generateDetails(metaInformation)
  }

  case class ObjectDocument(path: Path, metaInformation: MetaValue, value: HoconObject)
      extends Document[HoconObject]
      with DocumentEnvironments
      with DocumentReferences
      with DocumentUnresolvedReferences {

    import pl.onewebpro.hocones.parser.ops.HoconOps._

    val size: Int = value.values.size

    val environments: Iterable[EnvironmentValue] = value.values.extract[EnvironmentValue]

    val references: Iterable[ResolvedRef] = value.values.extract[ResolvedRef]

    val unresolvedReferences: Iterable[NotResolvedRef] = value.values.extract[NotResolvedRef]

    val details: Map[String, String] = generateDetails(metaInformation)
  }

  case class ReferenceValueDocument(path: Path, metaInformation: MetaValue, value: HoconReferenceValue)
      extends Document[HoconReferenceValue] {
    val details: Map[String, String] = generateDetails(metaInformation)
  }

  case class ResolvedReferenceDocument(path: Path, metaInformation: MetaValue, value: HoconResolvedReference)
      extends Document[HoconResolvedReference] {
    val details: Map[String, String] = generateDetails(metaInformation)

    val defaultValue: Option[DefaultValue] = value.value match {
      case merged: HoconMergedValues => merged.extractDefaultValue
      case _                         => None
    }
  }

  case class ValueDocument(path: Path, metaInformation: MetaValue, value: HoconValue) extends Document[HoconValue] {
    val quoted: Boolean = value.value.wasQuoted

    val details: Map[String, String] = generateDetails(metaInformation)

    val valueType: String = value.valueType match {
      case SimpleValueType.UNQUOTED_STRING => "text"
      case SimpleValueType.QUOTED_STRING   => "text"
      case SimpleValueType.BOOLEAN         => "boolean"
      case SimpleValueType.DOUBLE          => "double"
      case SimpleValueType.INT             => "integer"
      case SimpleValueType.LONG            => "long"
      case SimpleValueType.NULL            => "null"
    }
  }

}
