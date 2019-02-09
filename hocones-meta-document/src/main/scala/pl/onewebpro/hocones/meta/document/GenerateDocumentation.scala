package pl.onewebpro.hocones.meta.document

import cats.implicits._
import cats.effect.SyncIO
import pl.onewebpro.hocones.meta.document.model._
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.entity._

object GenerateDocumentation {

  def generate(result: HoconResult, meta: MetaInformation): SyncIO[Documentation] =
    SyncIO
      .pure(result.results)
      .flatMap { values =>
        values.map(mapToDocument(meta)).toList.sequence
      }
      .flatMap(generateDocumentation(meta))

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
        SyncIO.raiseError(MarkdownDocumentationError(s"No meta information for path ${result.path}"))
    }

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
}
