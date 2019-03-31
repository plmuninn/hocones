package pl.muninn.hocones.meta

import cats.effect.SyncIO
import pl.muninn.hocones.meta.model.MetaInformation

object MetaInformationMerger {

  private def updateOrphans(loaded: MetaInformation, generated: MetaInformation): SyncIO[Seq[model.MetaValue]] =
    SyncIO
      .pure(generated.orphans)
      .map(_.map { value =>
        loaded.orphans
          .find(orphan => orphan.name == value.name && orphan.getClass.getName == value.getClass.getName)
          .getOrElse(value)
      })

  def updateRoots(
    loaded: MetaInformation,
    generated: MetaInformation
  ): SyncIO[Map[String, Map[String, Seq[model.MetaValue]]]] =
    SyncIO(generated.roots).map(_.map {
      case (key, roots) =>
        key -> roots.map {
          case (innerKey, values) =>
            innerKey -> values.map { value =>
              loaded
                .findByPathAndName(s"$key.$innerKey.${value.name}")
                .filter(foundValue => foundValue.getClass.getName == value.getClass.getName)
                .getOrElse(value)
            }
        }
    })

  def merge(loaded: MetaInformation, generated: MetaInformation): SyncIO[MetaInformation] =
    for {
      orphans <- updateOrphans(loaded, generated)
      roots <- updateRoots(loaded, generated)
      merged <- SyncIO(generated.copy(orphans = orphans, roots = roots))
      sorted <- SyncIO(MetaInformation.sortMetaInformation(merged))
    } yield sorted
}
