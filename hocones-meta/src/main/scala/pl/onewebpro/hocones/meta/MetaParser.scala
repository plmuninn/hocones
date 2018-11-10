package pl.onewebpro.hocones.meta

import cats.effect.SyncIO
import pl.onewebpro.hocones.meta.BuildInfo.version
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconResult

object MetaParser {

  import pl.onewebpro.hocones.parser.ops.HoconOps._

  def generate(hocones: HoconResult): SyncIO[MetaInformation] =
    SyncIO.pure(MetaInformation(version, Map(
      "pl.onewebpro.test" -> Map(
        "values" -> Seq(
          MetaString("name", "description", "pattern", 0, 0),
          MetaNumber("name", "description", 0, 0),
          MetaBoolean("name", "description"),
          MetaList("name", "description", false, "element-type"),
          MetaObject("name", "description", "element-type"),
        )
      ),
      "com.onewebpro.test" -> Map(
        "others" -> Seq(
          MetaString("name", "description", "pattern", 0, 0),
          MetaNumber("name", "description", 0, 0),
          MetaBoolean("name", "description"),
          MetaList("name", "description", false, "element-type"),
          MetaObject("name", "description", "element-type"),
        )
      )
    ), Seq(
      MetaString("name", "description", "pattern", 0, 0),
      MetaNumber("name", "description", 0, 0),
      MetaBoolean("name", "description"),
      MetaList("name", "description", false, "element-type"),
      MetaObject("name", "description", "element-type"),
    ))) // TODO

  def resolve(loaded: MetaInformation, generated: MetaInformation): SyncIO[MetaInformation] =
    SyncIO.pure(generated) // TODO
}
