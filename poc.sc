
import $ivy.`com.typesafe:config:1.3.2`
import com.typesafe.config.{Config, ConfigFactory, ConfigRenderOptions, ConfigResolveOptions}
import ammonite.ops._

import scala.collection.JavaConverters._

//import $ivy.`net.steppschuh.markdowngenerator:markdowngenerator:1.3.1.1`
//import net.steppschuh.markdowngenerator.table.Table


val file = (pwd / "configs" / "test.conf").toIO
val config: Config = ConfigFactory.parseFile(file)

private val envNameRegex = """\$\{\??(\w*)\}""".r

//pprint.pprintln(envNameRegex.findAllIn("${TEST_VALUE}/dupa/dupa/${TEST_VALUE}").toList)

pprint.pprintln(config.entrySet().asScala.map { value =>
  val x = value.getValue.render(ConfigRenderOptions.concise().setFormatted(true)).lines.toList
//  x.map(_.replace("\"", "").replace(",", "")).foreach(println)
  (value.getKey, x, value.getValue.render(ConfigRenderOptions.concise().setFormatted(true)), value.getValue.getClass.getCanonicalName)
}, height = 500)