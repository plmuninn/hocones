package pl.onewebpro.hocones.md.document

import cats.effect.SyncIO
import cats.implicits._
import net.steppschuh.markdowngenerator.text.{Text, TextBuilder}
import net.steppschuh.markdowngenerator.{MarkdownBuilder, MarkdownSerializable}
import pl.onewebpro.hocones.md.MdFileError
import pl.onewebpro.hocones.md.document.model._
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._

class DocumentationGenerator(documentation: Documentation) {

  import pl.onewebpro.hocones.md.document.md.implicits._

  def documentToMd: Document[_] => MarkdownSerializable = {
    case model: ArrayDocument             => model.toMd
    case model: ConcatenationDocument     => model.toMd
    case model: EnvironmentDocument       => model.toMd
    case model: MergedValuesDocument      => model.toMd
    case model: ReferenceValueDocument    => model.toMd
    case model: ResolvedReferenceDocument => model.toMd
    case model: ValueDocument             => model.toMd
  }

  def toMd: SyncIO[String] =
    for {
      builder <- SyncIO(new TextBuilder().asInstanceOf[MarkdownBuilder[TextBuilder, Text]])
      builder <- SyncIO(documentation.roots.foldLeft(builder) {
        case (bb, (root, models)) =>
          models.foldLeft(
            bb.heading(root, 2)
              .asInstanceOf[MarkdownBuilder[TextBuilder, Text]]
          )((acc, element) => acc.append(documentToMd(element)).rule())
      })
      orphans <- SyncIO(documentation.orphans.map(documentToMd))
      builder <- SyncIO(orphans.foldLeft(builder)((acc, element) => acc.append(element).rule()))
    } yield builder.toString
}

object DocumentationGenerator {

  import pl.onewebpro.hocones.common.implicits._

  private[document] def generateDocumentation(
    metaInformation: MetaInformation
  )(documents: List[Document[_]]): SyncIO[Documentation] =
    for {
      orphans <- SyncIO(documents.filter(_.path.isOrphan))
      roots <- SyncIO(
        metaInformation.roots.keys
          .map(root => root -> documents.filter(document => document.path.contains(root)))
          .toMap
      )
    } yield Documentation(roots, orphans)

  private[document] def mapToDocument(metaInformation: MetaInformation)(result: HoconResultValue): SyncIO[Document[_]] =
    metaInformation.findByPathAndName(result.path) match {
      case Some(meta) =>
        result match {
          case model: HoconArray =>
            SyncIO.pure(ArrayDocument(model.path, meta, model))
          case model: HoconConcatenation =>
            SyncIO.pure(ConcatenationDocument(model.path, meta, model))
          case model: HoconEnvironmentValue =>
            SyncIO.pure(EnvironmentDocument(model.path, meta, model))
          case model: HoconMergedValues =>
            SyncIO.pure(MergedValuesDocument(model.path, meta, model))
          case model: HoconObject =>
            SyncIO.pure(ObjectDocument(model.path, meta, model))
          case model: HoconReferenceValue =>
            SyncIO.pure(ReferenceValueDocument(model.path, meta, model))
          case model: HoconResolvedReference =>
            SyncIO.pure(ResolvedReferenceDocument(model.path, meta, model))
          case model: HoconValue =>
            SyncIO.pure(ValueDocument(model.path, meta, model))
        }
      case None =>
        SyncIO.raiseError(MdFileError(s"No meta information for path ${result.path}"))
    }

  def apply(result: HoconResult, meta: MetaInformation): SyncIO[DocumentationGenerator] =
    SyncIO
      .pure(result.results)
      .flatMap { values =>
        values.map(mapToDocument(meta)).toList.sequence
      }
      .flatMap(generateDocumentation(meta))
      .map(documentation => new DocumentationGenerator(documentation))

}
