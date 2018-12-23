package pl.onewebpro.hocones.cli.arguments.docs
import cats.data.{Validated, ValidatedNel}
import com.monovore.decline.{Argument, Opts}
import pl.onewebpro.hocones.md.config.Configuration.TableAlignment.TableAlignment
import pl.onewebpro.hocones.md.config.Configuration.{TableAlignment => TA}

object TableAlignment {

  implicit private val alignmentArgument: Argument[TableAlignment] = new Argument[TableAlignment] {

    override def read(string: String): ValidatedNel[String, TableAlignment] = string match {
      case "left"   => Validated.valid(TA.Left)
      case "right"  => Validated.valid(TA.Right)
      case "center" => Validated.valid(TA.Center)
      case value    => Validated.invalidNel(s"Invalid value '$value'. Proper values are: left, right or center.")
    }

    override def defaultMetavar: String = "alignment"
  }

  val opts: Opts[TableAlignment] =
    Opts
      .option[TableAlignment](long = "alignment",
                              help = "alignment of values in table (left, right, center) - default left",
                              short = "a")
      .withDefault(TA.Left)

}
