package pl.muninn.hocones.common.codec
import cats.Functor
import cats.implicits._

trait HoconesCodec {

  import pl.muninn.hocones.common.codec.HoconesCodec.Codec

  implicit class EffectHoconesCodec[F[_]: Functor, A](F: F[A]) {
    def encodeTo[B](implicit codec: Codec[A, B]): F[B] = F.map(codec)
  }

  implicit class HoconesCodec[A](value: A) {
    def encodeTo[B](implicit codec: Codec[A, B]): B = codec(value)
  }

  def encodeTo[A, B](value: A)(implicit codec: Codec[A, B]): B = codec(value)

}

object HoconesCodec {
  type Codec[-A, B] = A => B
}
