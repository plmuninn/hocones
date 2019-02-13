package pl.onewebpro.hocones.meta.document

import pl.onewebpro.hocones.common.implicits.Path
import pl.onewebpro.hocones.meta.document.model.DocumentType.DocumentType
import pl.onewebpro.hocones.meta.model.MetaValue
import pl.onewebpro.hocones.parser.entity._

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

  case class Documentation(roots: Map[String, Seq[Document]], orphans: Seq[Document]) {

    def findByMetaValue[T <: MetaValue](value: T): Option[Document] =
      (roots.values.flatten ++ orphans).find { document =>
        document.metaInformation == value
      }
  }

  trait Document {
    type T <: HoconResultValue

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

    private def generateDetails[A <: MetaValue](meta: A): Map[String, String] =
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

    def details: Map[String, String] = generateDetails(metaInformation)
  }

  case class ArrayDocument(path: Path, metaInformation: MetaValue, value: HoconArray) extends Document {
    override type T = HoconArray
  }

  case class ConcatenationDocument(path: Path, metaInformation: MetaValue, value: HoconConcatenation) extends Document {
    override type T = HoconConcatenation
  }

  case class EnvironmentDocument(path: Path, metaInformation: MetaValue, value: HoconEnvironmentValue)
      extends Document {
    override type T = HoconEnvironmentValue
  }

  case class MergedValuesDocument(path: Path, metaInformation: MetaValue, value: HoconMergedValues) extends Document {
    override type T = HoconMergedValues
  }

  case class ObjectDocument(path: Path, metaInformation: MetaValue, value: HoconObject) extends Document {
    override type T = HoconObject
  }

  case class ReferenceValueDocument(path: Path, metaInformation: MetaValue, value: HoconReferenceValue)
      extends Document {
    override type T = HoconReferenceValue
  }

  case class ResolvedReferenceDocument(path: Path, metaInformation: MetaValue, value: HoconResolvedReference)
      extends Document {
    override type T = HoconResolvedReference
  }

  case class ValueDocument(path: Path, metaInformation: MetaValue, value: HoconValue) extends Document {
    override type T = HoconValue
  }

}
