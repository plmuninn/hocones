import com.typesafe.config.ConfigFactory
import sbt.plugins.JvmPlugin
import sbt._
import Keys._
import sbt.internal.inc.classpath.ClasspathUtilities

object HoconesPlugin extends AutoPlugin {

  // List of ignored paths - system paths basically
  lazy val ignoredSystemPaths = List(
    "gopherProxySet",
    "ftp",
    "os",
    "line",
    "sun",
    "path",
    "ssl-config",
    "file",
    "java",
    "jnidispatch",
    "jline",
    "http",
    "socksNonProxyHosts",
    "jna",
    "user",
    "awt",
  )

  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = JvmPlugin

  object autoImport extends HoconesKeys

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    unmanagedClasspath in Compile ++= (unmanagedResources in Compile).value,
    hocones := {
      // Probably we need some other classpaths?
      // * externalDependencyClasspath
      // * dependencyClasspath

      val classLoader = ClasspathUtilities.toLoader(Attributed.data((Compile / unmanagedClasspath).value))
      val loadedConfig = ConfigFactory.load(classLoader).root()
      val config = ignoredSystemPaths.foldLeft(loadedConfig) {
        case (cfg, path) => cfg.withoutKey(path)
      }

      println(config.render())
    },
    compile in Compile := (compile in Compile).dependsOn(hocones).value
  )

}
