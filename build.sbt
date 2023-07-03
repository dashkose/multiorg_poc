import Dependencies._
import Libraries._

Global / onChangedBuildSource := ReloadOnSourceChanges

initialize := {
  val _ = initialize.value // run the previous initialization
  val required = "1.8"
  val current  = sys.props("java.specification.version")
  assert(current == required, s"Unsupported JDK: java.specification.version $current[current version] != $required[required version]")
}

inThisBuild(
  List(
    organization := "me.xsight",
    scalaVersion := "2.12.17",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
  )
)

ThisBuild / scalacOptions ++=
  Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Ywarn-unused-import",
    "-language:higherKinds",
    "-Ypartial-unification"
  )


lazy val zioLoggingLibs= Seq(zioLogging, zioLoggingSlf4j, logBack)
lazy val zioConfigLibs = Seq(zioConfig, zioConfigTypesafe, zioConfigMagnolia)

lazy val zioLibs = Seq(zio, zioJson, zioKafka, zioPrelude, zioOptics, zioJGolden) ++
  zioConfigLibs ++
  zioLoggingLibs
lazy val kafkaLibs = Seq(kafka)
lazy val otherLibs = Seq(enumeratum)
lazy val sparkLibs = Seq(sparkSql, hadoopAws, hadoopClient, awsSdk, urlConnectionClient, glue, iceberg)

lazy val testLibs = Seq(zioTest, zioTestSbt, quickLens, embeddedKafka).map(_ % Test)

lazy val spark =
  project.in(file("spark"))
    .settings(Seq(
      resolvers ++= Seq("public", "snapshots", "releases").flatMap(Resolver.sonatypeOssRepos),
      version := sys.env.getOrElse("RELEASE_VERSION", "0.0.1"),
      assembly / assemblyJarName := s"dataverse-spark4s2-${version.value}.jar",
      assembly / assemblyMergeStrategy := {
        case x if Assembly.isConfigFile(x) =>
          MergeStrategy.concat
        case PathList(ps@_*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
          MergeStrategy.rename
        case PathList("META-INF", xs@_*) =>
          (xs map {
            _.toLowerCase
          }) match {
            case ("manifest.mf" :: Nil) | ("index.list" :: Nil) | ("dependencies" :: Nil) =>
              MergeStrategy.discard
            case ps@(x :: xs) if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
              MergeStrategy.discard
            case "plexus" :: xs =>
              MergeStrategy.discard
            case "services" :: xs =>
              MergeStrategy.filterDistinctLines
            case ("spring.schemas" :: Nil) | ("spring.handlers" :: Nil) =>
              MergeStrategy.filterDistinctLines
            case _ => MergeStrategy.first
          }
        case x if x.endsWith("module-info.class") => MergeStrategy.discard
        case _ => MergeStrategy.first
      },
      assembly / assemblyOption ~= {
        _.withIncludeScala(true)
      },
      libraryDependencies ++= sparkLibs ++ zioLibs ++ kafkaLibs ++ otherLibs ++ testLibs,
      excludeDependencies += "org.scala-lang.modules" % "scala-collection-compat_2.13",
      Compile / run := Defaults.runTask(Compile / fullClasspath, Compile / run / mainClass, Compile / run / runner).evaluated,
      Compile / runMain := Defaults.runMainTask(Compile / fullClasspath, Compile / run / runner).evaluated,
      Test / fork := true,
      Test / envVars := Map ("SOME_KEY" -> "someValue"),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
    ))
    .enablePlugins(JavaAppPackaging)

addCommandAlias("checkFmtAll", ";scalafmtSbtCheck;scalafmtCheckAll")
addCommandAlias("scalafixCheck", ";scalafix --check")
addCommandAlias("mutation", ";project scheduler;stryker;")
addCommandAlias("sanity", "clean;compile;scalafixAll;scalafmtAll;test")
addCommandAlias("ll", "projects")

//this should be added as part of sanity once we reach proper threshold
// coverageExcludedPackages := "<empty>;Reverse.*;.*AuthService.*;models\\.data\\..*"
coverageExcludedPackages := "Main.scala;zio\\.json\\..*"

addCommandAlias("cover", ";project scheduler;clean;coverage;test;coverageReport;")
