package pl.onewebpro.hocones.parser

import java.io.File

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.JavaConverters._

trait TestSpec extends FlatSpec with Matchers {

  def getEntriesMap(implicit config: Config): Map[String, ConfigValue] =
    config.entrySet().asScala.map(entry => entry.getKey -> entry.getValue).toMap

  def loadConfig(name: String): Config = {
    val filePath = getClass.getResource(s"/configs/$name").getFile
    val file = new File(filePath)
    ConfigFactory.parseFile(file)
  }
}
