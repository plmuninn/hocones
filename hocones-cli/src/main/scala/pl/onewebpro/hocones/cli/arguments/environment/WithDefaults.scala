package pl.onewebpro.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object WithDefaults {
  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "defaults",
        help =
          "defaults is boolean property - should default values of environment variables be set in file - default false",
        short = "d"
      )
      .orFalse
}
