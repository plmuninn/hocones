package pl.muninn.hocones.env.config

import java.nio.file.Path

object Configuration {

  case class EnvironmentConfiguration(
    outputPath: Path,
    withComments: Boolean,
    withDefaults: Boolean,
    removeDuplicates: Boolean
  )

}
