package pl.onewebpro.hocon.utils.parser.result

import com.typesafe.config.Config
//import pl.onewebpro.hocon.utils.parser.HoconParser.Path
import pl.onewebpro.hocon.utils.parser.entity.{/*HoconResultType,*/ HoconResultValue, Result}

case class HoconResult(cfg: Config, results: Iterable[Result]) {

//  //TODO move it from here and rename
//  private[parser] def flattenValues(withContainers: Boolean, values: Iterable[HoconResultValue]): Stream[(Path, HoconResultValue)] =
//    values.toStream.flatMap {
//      case resultType: HoconResultType =>
//        if (withContainers)
//          (resultType.path -> resultType) #:: flattenValues(withContainers, resultType.values)
//        else
//          flattenValues(withContainers, resultType.values)
//      case value => Stream(value.path -> value)
//    }
//
//  //TODO move it from here and rename
//  def flatten(withContainers: Boolean = true): Stream[(Path, HoconResultValue)] = flattenValues(withContainers, results)
//
//  //TODO move it from here and rename
//  def find(path: Path): Option[HoconResultValue] =
//    flatten().find {
//      case (listPath, _) => listPath == path
//    }.map {
//      case (_, value) => value
//    }

}
