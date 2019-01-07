package pl.onewebpro.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object WithDefaults {

  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "defaults",
        help = "should default values of environment variables be set in environment file - default no",
        short = "d"
      )
      .orFalse
}
