import sbt._

trait HoconesKeys {
  lazy val hocones = taskKey[Unit]("Re-run hocones")
  lazy val loadConfigPath = settingKey[Option[String]]("What path from config should be loaded")
  lazy val ignoredPaths = settingKey[Seq[String]]("What paths should be ignored")
  lazy val configFile = settingKey[File]("Which config file should be loaded - default is application.conf")
  lazy val pathForSave = settingKey[File]("Where config file should be saved")
  lazy val createEnvironmentFile = settingKey[Boolean]("Should environment file be created - default true")
  lazy val environmentFileWithComments = settingKey[Boolean]("Should environment file contain comments - default true")
  lazy val environmentFileWithDefaults = settingKey[Boolean]("Should environment file contain defaults - default true")
  lazy val environmentWithoutDuplicates = settingKey[Boolean]("Should environment file be created without duplicates - default true")

  lazy val createDocumentationFile = settingKey[Boolean]("Should full documentation file be created - default true")

  lazy val createEnvironmentDocumentationTableFile = settingKey[Boolean]("Should environment document table file be created - default true")
  lazy val environmentDocumentationFileTableAlignment = settingKey[String](
    "What alignment should be used in environment document - default left - possible values (left, right, center)"
  )

}
