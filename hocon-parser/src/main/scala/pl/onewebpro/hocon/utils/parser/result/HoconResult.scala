package pl.onewebpro.hocon.utils.parser.result

import com.typesafe.config.Config
import pl.onewebpro.hocon.utils.parser.entity.HoconResultValue

case class HoconResult(cfg: Config, results: Seq[HoconResultValue])
