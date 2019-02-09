package pl.onewebpro.hocones.parser

import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{FlatSpec, Matchers}

trait TestSpec extends FlatSpec with Matchers {
  def loadConfig(name: String): Config = {
    val filePath = getClass.getResource(s"/configs/$name").getFile
    val file = new File(filePath)
    ConfigFactory.parseFile(file)
  }
}
