package pl.onewebpro.hocones.statistics

import cats.effect.SyncIO
import cats.implicits._
import pl.onewebpro.hocones.parser.HoconResult
import pl.onewebpro.hocones.parser.ops.HoconOps._

case class StatisticsMeta(numOfPaths: Int,
                          numOfEnvironmentValues: Int,
                          numOfNotResolvedRef: Int,
                          numOfResolvedRef: Int)

object StatisticsMeta {

  private[statistics] def numOfPaths(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.flattenResultValues(true).keys.size)

  private[statistics] def numOfEnvironmentValues(
      hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.environmentValues.size)

  private[statistics] def numOfNotResolvedRef(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.notResolvedValues.size)

  private[statistics] def numOfResolvedRef(hocon: HoconResult): SyncIO[Int] =
    SyncIO(hocon.results.resolvedRefValues.size)

  def fromParsedHocon(result: HoconResult): SyncIO[StatisticsMeta] =
    (numOfPaths(result),
     numOfEnvironmentValues(result),
     numOfNotResolvedRef(result),
     numOfResolvedRef(result)).mapN(StatisticsMeta.apply)
}
