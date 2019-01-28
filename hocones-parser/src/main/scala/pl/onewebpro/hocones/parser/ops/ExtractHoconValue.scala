package pl.onewebpro.hocones.parser.ops

import pl.onewebpro.hocones.parser.entity._
import pl.onewebpro.hocones.parser.entity.simple.{ComposedConfigValue, SimpleHoconValue}

import scala.reflect.ClassTag

private[ops] object ExtractHoconValue {

  private[ops] def isValue[T <: SimpleHoconValue](result: Result)(implicit tag: ClassTag[T]): Boolean = result match {
    case ComposedConfigValue(_, composedValues) =>
      composedValues.exists(isValue[T])
    case _: T => true
    case _    => false
  }

  private[ops] def resultContainsValue[T <: SimpleHoconValue](result: Result)(implicit tag: ClassTag[T]): Boolean =
    result match {
      case HoconConcatenation(_, _, concatenatedValues) =>
        concatenatedValues.values.exists(isValue[T]) ||
        concatenatedValues.values.exists(resultContainsValue[T])
      case HoconMergedValues(_, _, default, replace) =>
        isValue[T](default) ||
        isValue[T](replace) ||
        resultContainsValue[T](default) ||
        resultContainsValue[T](replace)
      case HoconArray(_, _, arrayValues) =>
        arrayValues.exists(resultContainsValue[T])
      case HoconObject(_, _, objectValues) =>
        objectValues.exists(resultContainsValue[T])
      case HoconReferenceValue(_, _, referenceValue) => isValue[T](referenceValue)
      case HoconEnvironmentValue(_, _, environmentValue) =>
        isValue[T](environmentValue)
      case HoconValue(_, _, _, value) => isValue[T](value)
      case _                          => false
    }

  private[ops] def extractValues[T <: SimpleHoconValue](
    values: HoconResultValue
  )(implicit tag: ClassTag[T]): Iterable[T] = values match {
    case HoconConcatenation(_, _, concatenatedValues) =>
      concatenatedValues.values.flatMap {
        case value: T => Iterable(value)
        case _        => Nil
      }
    case HoconMergedValues(_, _, default, replace) =>
      Iterable(default, replace).flatMap {
        case value: T                => Iterable(value)
        case value: HoconResultValue => extractValues[T](value)
        case _                       => Nil
      }
    case HoconArray(_, _, arrayValues) => arrayValues.flatMap(extractValues[T])
    case HoconObject(_, _, objectValues) =>
      objectValues.flatMap(extractValues[T])
    case HoconEnvironmentValue(_, _, value: T) => Iterable(value)
    case HoconValue(_, _, _, value: T)         => Iterable(value)
    case HoconReferenceValue(_, _, value: T)   => Iterable(value)
    case _                                     => Nil
  }

  def toExtractedValues[T <: SimpleHoconValue](
    implicit tag: ClassTag[T]
  ): HoconResultValue => Option[ExtractedValue[T]] = { value =>
    if (!resultContainsValue(value)) None
    else Some(ExtractedValue[T](value.cfg, value, extractValues[T](value)))
  }

}
