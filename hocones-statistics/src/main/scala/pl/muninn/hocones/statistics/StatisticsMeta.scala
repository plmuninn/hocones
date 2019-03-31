package pl.muninn.hocones.statistics

import cats.effect.SyncIO
import cats.implicits._
import pl.muninn.hocones.parser.HoconResult
import pl.muninn.hocones.parser.entity.simple.{EnvironmentValue, NotResolvedRef, ResolvedRef}

case class StatisticsMeta(numOfPaths: Int, numOfEnvironmentValues: Int, numOfNotResolvedRef: Int, numOfResolvedRef: Int)

object StatisticsMeta {

  import pl.muninn.hocones.parser.ops.HoconOps._

  private[statistics] def numOfPaths(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.flattenResultValues(true).keys.size)

  private[statistics] def numOfEnvironmentValues(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.extract[EnvironmentValue].size)

  private[statistics] def numOfNotResolvedRef(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.extract[NotResolvedRef].size)

  private[statistics] def numOfResolvedRef(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.extract[ResolvedRef].size)

  def fromParsedHocon(result: HoconResult): SyncIO[StatisticsMeta] =
    (numOfPaths(result), numOfEnvironmentValues(result), numOfNotResolvedRef(result), numOfResolvedRef(result))
      .mapN(StatisticsMeta.apply)
}
