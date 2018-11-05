package pl.onewebpro.hocones.meta.model

import _root_.io.circe._
import _root_.io.circe.Decoder._

object JsonCodecs {

  private val hoconVersion = "hocones-version"
  private val children = "children"
  private val name = "name"
  private val description = "description"

  private val encodeMetaChildMap: Encoder[Map[String, List[MetaChild]]] =
    new Encoder[Map[String, List[MetaChild]]] {
      override def apply(a: Map[String, List[MetaChild]]): Json =
        if (a.isEmpty) Json.Null else Encoder.encodeMap[String, List[MetaChild]].apply(a)
    }

  implicit val metaInformationEncoder: Encoder[MetaInformation] =
    new Encoder[MetaInformation] {
      override def apply(meta: MetaInformation): Json =
        Json.obj(
          hoconVersion -> Json.fromString(meta.hoconesVersion),
          children -> encodeMetaChildMap(meta.children)
        )
    }

  implicit val metaChildEncoder: Encoder[MetaChild] =
    new Encoder[MetaChild] {
      override def apply(a: MetaChild): Json =
        Json.obj(
          name -> Json.fromString(a.name),
          description -> Json.fromString(a.description),
          children -> encodeMetaChildMap(a.children)
        )
    }

  private implicit val decodeMetaChildMap: Decoder[Map[String, List[MetaChild]]] =
    new Decoder[Map[String, List[MetaChild]]] {
      override def apply(c: HCursor): Result[Map[String, List[MetaChild]]] =
        if (c.value.isNull) Right(Map.empty) else Decoder.decodeMap[String, List[MetaChild]].apply(c)
    }

  implicit val metaInformationDecoder: Decoder[MetaInformation] =
    new Decoder[MetaInformation] {
      override def apply(c: HCursor): Result[MetaInformation] = for {
        hoconVersion <- c.downField(hoconVersion).as[String]
        children <- c.getOrElse[Map[String, List[MetaChild]]](children)(Map.empty)(decodeMetaChildMap)
      } yield MetaInformation(hoconVersion, children)
    }

  implicit val metaChildDecoder: Decoder[MetaChild] =
    new Decoder[MetaChild] {
      override def apply(c: HCursor): Result[MetaChild] = for {
        name <- c.downField(name).as[String]
        description <- c.downField(description).as[String]
        children <- c.getOrElse[Map[String, List[MetaChild]]](children)(Map.empty)(decodeMetaChildMap)
      } yield MetaChild(name, description, children)
    }

}