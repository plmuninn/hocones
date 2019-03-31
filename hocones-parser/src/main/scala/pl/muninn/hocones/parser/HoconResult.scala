package pl.muninn.hocones.parser

import com.typesafe.config.Config
import pl.muninn.hocones.parser.entity.HoconResultValue

case class HoconResult(cfg: Config, results: Seq[HoconResultValue])
