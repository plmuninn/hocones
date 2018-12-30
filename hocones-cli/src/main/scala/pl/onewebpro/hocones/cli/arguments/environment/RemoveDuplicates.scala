package pl.onewebpro.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object RemoveDuplicates {
  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "remove-duplicates",
        help =
          "remove-duplicates is boolean property - should duplicates be removed from output file - default false",
        short = "r")
      .orFalse
}
