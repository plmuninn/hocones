package pl.onewebpro.hocones.meta

import cats.effect.SyncIO
import pl.onewebpro.hocones.meta.BuildInfo.version
import pl.onewebpro.hocones.meta.model._
import pl.onewebpro.hocones.parser.HoconResult

object MetaParser {

  def generate(hocones: HoconResult): SyncIO[MetaInformation] =
    SyncIO.pure(MetaInformation(version, Map(
      "pl.onewebpro.test" -> Map(
        "values" -> Seq(
          MetaString("name1", "description", "pattern", 0, 0),
          MetaNumber("name2", "description", 0, 0),
          MetaBoolean("name3", "description"),
          MetaList("name4", "description", false, "element-type"),
          MetaObject("name5", "description", "element-type"),
        )
      ),
      "com.onewebpro.test" -> Map(
        "others" -> Seq(
          MetaString("name6", "description", "pattern", 0, 0),
          MetaNumber("name7", "description", 0, 0),
          MetaBoolean("name8", "description"),
          MetaList("name9", "description", false, "element-type"),
          MetaObject("name10", "description", "element-type"),
        )
      )
    ), Seq(
      MetaString("name11", "description", "pattern", 0, 0),
      MetaNumber("name12", "description", 0, 0),
      MetaBoolean("name13", "description"),
      MetaList("name14", "description", false, "element-type"),
      MetaObject("name15", "description", "element-type"),
    ))) // TODO
}
