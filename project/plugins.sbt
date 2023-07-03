ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
) //Needed to resolve version conflict for scala-xml https://github.com/sbt/sbt/issues/7007

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.9.11")
addSbtPlugin("io.stryker-mutator" % "sbt-stryker4s" % "0.14.3")
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.10.4")
addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.3.2")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "2.0.6")
addSbtPlugin("nl.gn0s1s" % "sbt-dotenv" % "3.0.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.0")
addDependencyTreePlugin
