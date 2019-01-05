package pl.onewebpro.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object DisplayMeta {
  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "meta-information",
        help =
          "meta is boolean property - should meta information of environment variables be set in file - default false",
        short = "m"
      )
      .orFalse
}
