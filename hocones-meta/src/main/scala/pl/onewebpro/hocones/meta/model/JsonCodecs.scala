package pl.onewebpro.hocones.meta.model

import cats.implicits._
import _root_.io.circe._
import _root_.io.circe.Decoder._
import _root_.io.circe.generic.semiauto._

object JsonCodecs {

  private val hoconVersionK = "hocones-version"
  private val rootsK = "roots"
  private val orphansK = "orphans"

  implicit val metaStringEncoder: Encoder[MetaString] = deriveEncoder[MetaString]
  implicit val metaNumberEncoder: Encoder[MetaNumber] = deriveEncoder[MetaNumber]
  implicit val metaBooleanEncoder: Encoder[MetaBoolean] = deriveEncoder[MetaBoolean]
  implicit val metaListEncoder: Encoder[MetaList] = deriveEncoder[MetaList]
  implicit val metaObjectEncoder: Encoder[MetaObject] = deriveEncoder[MetaObject]
  implicit val metaUntypeEncoder: Encoder[MetaUntypeInformation] = deriveEncoder[MetaUntypeInformation]
  implicit val metaConcatenationEncoder: Encoder[MetaConcatenation] = deriveEncoder[MetaConcatenation]
  implicit val metaEnvironmentEncoder: Encoder[MetaEnvironment] = deriveEncoder[MetaEnvironment]

  implicit private val encodeMetaValue: Encoder[MetaValue] =
    new Encoder[MetaValue] {
      override def apply(a: MetaValue): Json = a match {
        case model: MetaString => metaStringEncoder.apply(model)
        case model: MetaNumber => metaNumberEncoder.apply(model)
        case model: MetaBoolean => metaBooleanEncoder.apply(model)
        case model: MetaList => metaListEncoder.apply(model)
        case model: MetaObject => metaObjectEncoder.apply(model)
      }
    }

  private val encodeMetaRootsMap: Encoder[Map[String, Map[String, Seq[MetaValue]]]] =
    new Encoder[Map[String, Map[String, Seq[MetaValue]]]] {
      override def apply(a: Map[String, Map[String, Seq[MetaValue]]]): Json =
        if (a.isEmpty) Json.Null else Encoder.encodeMap[String, Map[String, Seq[MetaValue]]].apply(a)
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

  implicit val decodeStringEncoder: Decoder[MetaString] = deriveDecoder[MetaString]
  implicit val decodeNumberEncoder: Decoder[MetaNumber] = deriveDecoder[MetaNumber]
  implicit val decodeBooleanEncoder: Decoder[MetaBoolean] = deriveDecoder[MetaBoolean]
  implicit val decodeListEncoder: Decoder[MetaList] = deriveDecoder[MetaList]
  implicit val decodeObjectEncoder: Decoder[MetaObject] = deriveDecoder[MetaObject]
  implicit val metaUntypeDecoder: Decoder[MetaUntypeInformation] = deriveDecoder[MetaUntypeInformation]
  implicit val metaConcatenationDecoder: Decoder[MetaConcatenation] = deriveDecoder[MetaConcatenation]
  implicit val metaEnvironmentDecoder: Decoder[MetaEnvironment] = deriveDecoder[MetaEnvironment]

  private implicit val metaValueDecoder: Decoder[MetaValue] =
    new Decoder[MetaValue] {
      override def apply(c: HCursor): Result[MetaValue] =
        decodeStringEncoder.apply(c)
          .orElse(decodeStringEncoder.apply(c))
          .orElse(decodeListEncoder.apply(c))
          .orElse(decodeObjectEncoder.apply(c))
          .orElse(decodeBooleanEncoder.apply(c))
    }

  private implicit val decodeMetaChildMap: Decoder[Map[String, Map[String, Seq[MetaValue]]]] =
    new Decoder[Map[String, Map[String, Seq[MetaValue]]]] {
      override def apply(c: HCursor): Result[Map[String, Map[String, Seq[MetaValue]]]] =
        if (c.value.isNull) Right(Map.empty) else Decoder.decodeMap[String, Map[String, Seq[MetaValue]]].apply(c)
    }

  implicit val metaInformationDecoder: Decoder[MetaInformation] =
    new Decoder[MetaInformation] {
      override def apply(c: HCursor): Result[MetaInformation] = for {
        hoconVersion <- c.downField(hoconVersionK).as[String]
        roots <- c.getOrElse[Map[String, Map[String, Seq[MetaValue]]]](rootsK)(Map.empty)(decodeMetaChildMap)
        orphans <- c.getOrElse[Seq[MetaValue]](orphansK)(Seq.empty)
      } yield MetaInformation(hoconVersion, roots, orphans)
    }
}