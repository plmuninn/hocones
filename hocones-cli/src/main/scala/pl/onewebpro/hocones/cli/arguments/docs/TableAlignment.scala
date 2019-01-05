package pl.onewebpro.hocones.cli.arguments.docs

import cats.data.{Validated, ValidatedNel}
import com.monovore.decline.{Argument, Opts}
import pl.onewebpro.hocones.md.config.Configuration.TableAlignment.TableAlignment
import pl.onewebpro.hocones.md.config.Configuration.{TableAlignment => MdTableAlignment}

object TableAlignment {

  val defaultAlignment: TableAlignment = MdTableAlignment.Left

  implicit private val alignmentArgument: Argument[TableAlignment] =
    new Argument[TableAlignment] {

      override def read(string: String): ValidatedNel[String, TableAlignment] =
        string match {
          case "left"   => Validated.valid(MdTableAlignment.Left)
          case "right"  => Validated.valid(MdTableAlignment.Right)
          case "center" => Validated.valid(MdTableAlignment.Center)
          case value =>
            Validated.invalidNel(s"Invalid value '$value'. Proper values are: left, right or center.")
        }

      override def defaultMetavar: String = "alignment"
    }

  val opts: Opts[TableAlignment] =
    Opts
      .option[TableAlignment](
        long = "alignment",
        help = "alignment of values in table (left, right, center) - default left",
        short = "a"
      )
      .withDefault(defaultAlignment)

}
