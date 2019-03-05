import sbt._

trait HoconesKeys {
  lazy val hocones = taskKey[Unit]("Run hocones")
}
