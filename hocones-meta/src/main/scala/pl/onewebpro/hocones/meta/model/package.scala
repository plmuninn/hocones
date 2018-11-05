package pl.onewebpro.hocones.meta

package object model {

  case class MetaInformation(hoconesVersion: String, children: Map[String, List[MetaChild]])

  case class MetaChild(name: String, description: String, children: Map[String, List[MetaChild]])

}
