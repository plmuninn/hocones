package pl.onewebpro.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object WithComments {
  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "comments",
        help =
          "comments is boolean property - should comments about environment variables be printed in file - default false",
        short = "c")
      .orFalse
}
