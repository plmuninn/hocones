package pl.muninn.hocones.cli.arguments.environment

import com.monovore.decline.Opts

object WithComments {

  val opts: Opts[Boolean] =
    Opts
      .flag(
        long = "comments",
        help = "should comments about environment variables be printed in environment file - default no",
        short = "c"
      )
      .orFalse
}
