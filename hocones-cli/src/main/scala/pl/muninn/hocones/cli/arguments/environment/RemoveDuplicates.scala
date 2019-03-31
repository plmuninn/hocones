package pl.muninn.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object RemoveDuplicates {

  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "remove-duplicates",
        help = "should duplicates be removed from environment output file - default no",
        short = "r"
      )
      .orFalse
}
