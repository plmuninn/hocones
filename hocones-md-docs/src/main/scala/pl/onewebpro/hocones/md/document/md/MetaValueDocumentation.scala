package pl.onewebpro.hocones.md.document.md

import pl.onewebpro.hocones.meta.model._

import scala.language.implicitConversions

trait MetaValueDocumentation {
  // Flatten for Options
  implicit private def flat[K, V](kv: (K, Option[V])) =
    kv._2.map(kv._1 -> _).toList

  // TODO use shapless
  def getDetails: Option[MetaValue] => Map[String, String] = {
    case Some(model: MetaString) =>
      Map("pattern" -> model.pattern,
          "min-length" -> model.`min-length`.map(_.toString),
          "max-length" -> model.`max-length`.map(_.toString)).flatten.toMap
    case Some(model: MetaNumber) =>
      Map("max-value" -> model.`max-value`.map(_.toString),
          "min-value" -> model.`min-value`.map(_.toString)).flatten.toMap
    case Some(model: MetaList) =>
      Map("can-be-empty" -> model.`can-be-empty`.map(_.toString),
          "element-type" -> model.`element-type`).flatten.toMap
    case Some(model: MetaObject) =>
      Map("element-type" -> model.`element-type`).flatten.toMap
    case Some(_) => Map.empty
    case None    => Map.empty
  }
}
