package pl.onewebpro.hocon.utils.parser

import cats.effect.IO
import pl.onewebpro.hocon.utils.parser.entity._
import pl.onewebpro.hocon.utils.parser.entity.simple._

private[parser] object HoconReferenceResolver {

  import pl.onewebpro.hocon.utils.parser.ops.HoconOps._

  private[parser] def resolveReferenceValueAsHoconEnvironmentValue(hrv: HoconReferenceValue): Option[HoconEnvironmentValue] =
    notResolvedReferenceToEnvironmentValue(hrv.result)
      .map(environmentValue => HoconEnvironmentValue(hrv.path, hrv.cfg, environmentValue))

  private[parser] def resolveReferenceValue(resultList: FlatResultList, hrv: HoconReferenceValue): HoconResultValue =
    resultList
      .findByPath(hrv.result.name)
      .map(value => HoconResolvedReference(value, hrv))
      .orElse(resolveReferenceValueAsHoconEnvironmentValue(hrv))
      .getOrElse(hrv)

  private[parser] def resolveResult(resultList: FlatResultList, value: Result): Result =
    value match {
      case notResolvedRef: NotResolvedRef => resolveSimpleValue(resultList, notResolvedRef)
      case composedConfigValue: ComposedConfigValue =>
        composedConfigValue.copy(values = composedConfigValue.values.map(configValue => resolveSimpleValue(resultList, configValue)))
      case arr: HoconArray => arr.copy(values = resolveReferences(resultList, arr.values))
      case obj: HoconObject => obj.copy(values = resolveReferences(resultList, obj.values))
      case hrv: HoconReferenceValue => resolveReferenceValue(resultList, hrv)
      case merged: HoconMergedValues =>
        merged.copy(defaultValue = resolveResult(resultList, merged.defaultValue), replacedValue = resolveResult(resultList, merged.replacedValue))
      case concatenation: HoconConcatenation =>
        concatenation.copy(value = concatenation.value.copy(values = concatenation.value.values.map(value => resolveSimpleValue(resultList, value))))
      case default => default
    }

  private[parser] def notResolvedReferenceToEnvironmentValue(notResolvedRef: NotResolvedRef): Option[EnvironmentValue] =
    notResolvedRef.nameChunks.lastOption.filter(name => name == name.toUpperCase).map(_ => EnvironmentValue(notResolvedRef))

  private[parser] def resolveSimpleValue(resultList: FlatResultList, value: SimpleHoconValue): SimpleHoconValue =
    value match {
      case notResolvedRef: NotResolvedRef =>
        resultList
          .findByPath(notResolvedRef.name)
          .map(value => ResolvedRef(value, notResolvedRef))
          .orElse(notResolvedReferenceToEnvironmentValue(notResolvedRef))
          .getOrElse(notResolvedRef)

      case composedConfigValue: ComposedConfigValue =>
        composedConfigValue.copy(values = composedConfigValue.values.map(configValue => resolveSimpleValue(resultList, configValue)))
      case default => default
    }

  def resolveReferences(resultList: FlatResultList, values: Seq[HoconResultValue]): Seq[HoconResultValue] =
    values
      .map {
        case arr: HoconArray => arr.copy(values = resolveReferences(resultList, arr.values))
        case obj: HoconObject => obj.copy(values = resolveReferences(resultList, obj.values))
        case hrv: HoconReferenceValue => resolveReferenceValue(resultList, hrv)
        case merged: HoconMergedValues =>
          merged.copy(defaultValue = resolveResult(resultList, merged.defaultValue), replacedValue = resolveResult(resultList, merged.replacedValue))
        case concatenation: HoconConcatenation =>
          concatenation.copy(value = concatenation.value.copy(values = concatenation.value.values.map(value => resolveSimpleValue(resultList, value))))
        case default => default
      }


  def apply(values: Iterable[HoconResultValue]): IO[Seq[HoconResultValue]] = for {
    asSeq <- IO(values.toSeq)
    resultList <- IO(asSeq.flattenResultValues(withContainers = true))
    result <- IO(resolveReferences(resultList, asSeq))
  } yield result
}
