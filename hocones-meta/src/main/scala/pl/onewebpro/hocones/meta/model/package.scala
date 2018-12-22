package pl.onewebpro.hocones.meta

import scala.collection.immutable.ListMap

package object model {

  case class MetaInformation(hoconesVersion: String,
                             roots: Map[String, Map[String, Seq[MetaValue]]],
                             orphans: Seq[MetaValue]) {

    def findByName(name: String): Option[MetaValue] =
      orphans
        .find(_.name == name)
        .orElse(roots.values.flatMap(_.values.flatten).find(_.name == name))

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
      //com.nordea.arc.craft.access-service.itss.user-id
      val splitted = pathWithName.split("\\.")

      if (splitted.length <= 2) findByName(pathWithName)
      else {
        val name = splitted.last
        val path = splitted.dropRight(1).mkString(".")

        findByPathAndName(path, name)
      }
    }

  }

  object MetaInformation {
    def sortMetaInformation(information: MetaInformation): MetaInformation = {
      val sortedRoots = ListMap(
        information.roots.toSeq
          .map {
            case (key, values) =>
              key -> ListMap(
                values.toSeq
                  .map({
                    case (subKey, elements) => subKey -> elements.sortBy(_.name)
                  })
                  .sortBy(_._1): _*)
          }
          .sortBy(_._1): _*)

      val sortedOrphans = information.orphans.sortBy(_.name)

      information.copy(roots = sortedRoots, orphans = sortedOrphans)
    }
  }

  trait MetaValue {
    def name: String

    def description: Option[String]
  }

  case class MetaGenericInformation(name: String, description: Option[String])
      extends MetaValue

  case class MetaString(name: String,
                        description: Option[String],
                        pattern: Option[String],
                        `min-length`: Option[Int],
                        `max-length`: Option[Int])
      extends MetaValue

  case class MetaNumber(name: String,
                        description: Option[String],
                        `max-value`: Option[Int],
                        `min-value`: Option[Int])
      extends MetaValue

  case class MetaList(name: String,
                      description: Option[String],
                      `can-be-empty`: Option[Boolean],
                      `element-type`: Option[String])
      extends MetaValue

  case class MetaObject(name: String,
                        description: Option[String],
                        `element-type`: Option[String])
      extends MetaValue

}
