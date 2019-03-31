resolvers += Resolver.sonatypeRepo("releases")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.8")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.18")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "1.2.10" )

addSbtPlugin("com.47deg"  % "sbt-microsites" % "0.9.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
