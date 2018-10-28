package pl.onewebpro.hocones.parser

import com.typesafe.config.Config
import pl.onewebpro.hocones.parser.entity.HoconResultValue

case class HoconResult(cfg: Config, results: Seq[HoconResultValue])
