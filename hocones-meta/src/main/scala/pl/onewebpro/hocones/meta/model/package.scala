package pl.onewebpro.hocones.meta

package object model {

  case class MetaInformation(hoconesVersion: String,
                             roots: Map[String, Map[String, Seq[MetaValue]]],
                             orphans: Seq[MetaValue]) {

    def findByName(name: String): Option[MetaValue] =
      orphans.find(_.name == name).orElse(roots.values.flatMap(_.values.flatten).find(_.name == name))

    def findByPath(path: String): Seq[MetaValue] = {
      val result = for {
        (sourcePath, source) <- roots
        (innerPath, values) <- source
      } yield {
        val valuesPaths = s"$sourcePath.$innerPath"
        if (valuesPaths == path) Some(values) else None
      }

      result.flatten.flatten.toSeq
    }

    def findByPathAndName(path: String, name: String): Option[MetaValue] =
      findByPath(path).find(_.name == name)

    def findByPathAndName(pathWithName: String): Option[MetaValue] = {
      val splitted = pathWithName.split("\\.")

      if (splitted.size == 1) findByName(splitted.head) else {
        val name = splitted.last
        val path = splitted.dropRight(1).mkString(".")

        findByPathAndName(path, name)
      }
    }

  }

  trait MetaValue {
    def name: String

    def description: String
  }

  case class MetaString(name: String,
                        description: String,
                        pattern: String,
                        `min-length`: Int,
                        `max-length`: Int) extends MetaValue

  case class MetaNumber(name: String,
                        description: String,
                        `max-value`: Int,
                        `min-value`: Int) extends MetaValue

  case class MetaBoolean(name: String,
                         description: String) extends MetaValue

  case class MetaList(name: String,
                      description: String,
                      `can-be-empty`: Boolean,
                      `element-type`: String) extends MetaValue

  case class MetaObject(name: String,
                        description: String,
                        `element-type`: String) extends MetaValue

}
