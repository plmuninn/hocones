package pl.onewebpro.hocones.meta.model

import _root_.io.circe._
import _root_.io.circe.Decoder._
import _root_.io.circe.generic.semiauto._

object JsonCodecs {

  private val hoconVersionK = "hocones-version"
  private val rootsK = "roots"
  private val orphansK = "orphans"

  implicit val metaStringEncoder: Encoder[MetaString] =
    deriveEncoder[MetaString]
  implicit val metaNumberEncoder: Encoder[MetaNumber] =
    deriveEncoder[MetaNumber]
  implicit val metaListEncoder: Encoder[MetaList] = deriveEncoder[MetaList]
  implicit val metaObjectEncoder: Encoder[MetaObject] =
    deriveEncoder[MetaObject]
  implicit val metaGenericEncoder: Encoder[MetaGenericInformation] =
    deriveEncoder[MetaGenericInformation]

  implicit private val encodeMetaValue: Encoder[MetaValue] =
    new Encoder[MetaValue] {
      override def apply(a: MetaValue): Json = a match {
        case model: MetaString             => metaStringEncoder.apply(model)
        case model: MetaNumber             => metaNumberEncoder.apply(model)
        case model: MetaList               => metaListEncoder.apply(model)
        case model: MetaObject             => metaObjectEncoder.apply(model)
        case model: MetaGenericInformation => metaGenericEncoder.apply(model)
      }
    }

  private val encodeMetaRootsMap: Encoder[Map[String, Map[String, Seq[MetaValue]]]] =
    new Encoder[Map[String, Map[String, Seq[MetaValue]]]] {
      override def apply(a: Map[String, Map[String, Seq[MetaValue]]]): Json =
        if (a.isEmpty) Json.Null
        else Encoder.encodeMap[String, Map[String, Seq[MetaValue]]].apply(a)
    }

  private val encodeOrphans: Encoder[Seq[MetaValue]] =
    new Encoder[Seq[MetaValue]] {
      override def apply(a: Seq[MetaValue]): Json =
        if (a.isEmpty) Json.Null else Encoder.encodeSeq[MetaValue].apply(a)
    }

  implicit val metaInformationEncoder: Encoder[MetaInformation] =
    new Encoder[MetaInformation] {
      override def apply(meta: MetaInformation): Json =
        Json.obj(
          hoconVersionK -> Json.fromString(meta.hoconesVersion),
          rootsK -> encodeMetaRootsMap(meta.roots),
          orphansK -> encodeOrphans(meta.orphans)
        )
    }

  implicit val decodeStringEncoder: Decoder[MetaString] =
    deriveDecoder[MetaString]
  implicit val decodeNumberEncoder: Decoder[MetaNumber] =
    deriveDecoder[MetaNumber]
  implicit val decodeListEncoder: Decoder[MetaList] = deriveDecoder[MetaList]
  implicit val decodeObjectEncoder: Decoder[MetaObject] =
    deriveDecoder[MetaObject]
  implicit val metaGenericDecoder: Decoder[MetaGenericInformation] =
    deriveDecoder[MetaGenericInformation]

  // TODO change decoding or use shaplesss for it
  implicit private val metaValueDecoder: Decoder[MetaValue] =
    new Decoder[MetaValue] {
      override def apply(c: HCursor): Result[MetaValue] =
        c.keys match {
          case Some(keys) =>
            val keysS = keys.toSeq
            if (keysS.contains("pattern")) decodeStringEncoder.apply(c)
            else if (keysS.contains("max-value")) decodeNumberEncoder.apply(c)
            else if (keysS.contains("can-be-empty")) decodeListEncoder.apply(c)
            else if (keysS.contains("element-type"))
              decodeObjectEncoder.apply(c)
            else metaGenericDecoder.apply(c)
          case None => Left(DecodingFailure("Keys empty", Nil))
        }
    }

  implicit private val decodeMetaChildMap: Decoder[Map[String, Map[String, Seq[MetaValue]]]] =
    new Decoder[Map[String, Map[String, Seq[MetaValue]]]] {
      override def apply(c: HCursor): Result[Map[String, Map[String, Seq[MetaValue]]]] =
        if (c.value.isNull) Right(Map.empty)
        else Decoder.decodeMap[String, Map[String, Seq[MetaValue]]].apply(c)
    }

  implicit val metaInformationDecoder: Decoder[MetaInformation] =
    new Decoder[MetaInformation] {
      override def apply(c: HCursor): Result[MetaInformation] =
        for {
          hoconVersion <- c.downField(hoconVersionK).as[String]
          roots <- c.getOrElse[Map[String, Map[String, Seq[MetaValue]]]](rootsK)(Map.empty)(decodeMetaChildMap)
          orphans <- c.getOrElse[Seq[MetaValue]](orphansK)(Seq.empty)
        } yield MetaInformation(hoconVersion, roots, orphans)
    }
}
