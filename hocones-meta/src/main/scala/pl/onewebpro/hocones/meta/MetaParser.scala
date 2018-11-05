package pl.onewebpro.hocones.meta

import cats.effect.SyncIO
import pl.onewebpro.hocones.meta.BuildInfo.version
import pl.onewebpro.hocones.meta.model.MetaInformation
import pl.onewebpro.hocones.parser.HoconResult

object MetaParser {

  def generate(hocones: HoconResult): SyncIO[MetaInformation] =
    SyncIO.pure(MetaInformation(version, Map.empty)) // TODO

  def resolve(loaded: MetaInformation, generated: MetaInformation): SyncIO[MetaInformation] =
    SyncIO.pure(generated) // TODO
}
