import sbt._

object Dependencies {

  object Versions {
    val zioV = "2.0.2"
    val zioConfigV = "3.0.2"
    val zioLoggingV = "2.1.0"
    val zioJsonV = "0.4.2"
    val zioKafkaV = "2.0.1"
    val zioOpticsV = "2.0.0-RC4"
    val kafkaV = "3.2.1"
    val logBackV = "1.1.2"
    val zioPreludeV = "1.0.0-RC16"
    val quickLensV = "1.9.0"
    val enumeratumV = "1.7.2"
    val jawnParserV = "1.3.2"
    val softwareAwsSdkV = "2.17.202"
    val skdHttpClientV = "2.18.27"
    val slf4jV = "1.7.36"

    val sparkV = "3.3.0"
    val hadoopV = "3.2.1"
    val icebergV = "1.1.0"
    val nessieV = "0.42.0"
  }

  object Libraries {

    import Versions._

    val zio               = "dev.zio" %% "zio"                          % zioV
    val zioConfig         = "dev.zio" %% "zio-config"                   % zioConfigV
    val zioConfigTypesafe = "dev.zio" %% "zio-config-typesafe"          % zioConfigV
    val zioConfigMagnolia = "dev.zio" %% "zio-config-magnolia"          % zioConfigV
    val zioKafka          = "dev.zio" %% "zio-kafka"                    % zioKafkaV
    val zioJson           = "dev.zio" %% "zio-json"                     % zioJsonV
    val zioJGolden        = "dev.zio" %% "zio-json-golden"              % zioJsonV
    val zioOptics         = "dev.zio" %% "zio-optics"                   % zioOpticsV
    val zioTest           = "dev.zio" %% "zio-test"                     % zioV
    val zioTestSbt        = "dev.zio" %% "zio-test-sbt"                 % zioV
    val zioLogging        = "dev.zio" %% "zio-logging"                  % zioLoggingV
    val zioLoggingSlf4j   = "dev.zio" %% "zio-logging-slf4j"            % zioLoggingV
    val zioPrelude        = "dev.zio" %% "zio-prelude"                  % zioPreludeV

    val quickLens     = "com.softwaremill.quicklens"    %% "quicklens"         % quickLensV
    val enumeratum    = "com.beachape"                  %% "enumeratum"        % enumeratumV

    val embeddedKafka = ("io.github.embeddedkafka"      %% "embedded-kafka"       % kafkaV).cross(CrossVersion.for3Use2_13)
    val logBack       = "ch.qos.logback"                % "logback-classic"       % logBackV
    val kafka         = "org.apache.kafka"              % "kafka-clients"         % kafkaV
    //val slf4j         = "org.slf4j"                     % "slf4j-simple"          % slf4jV

    val sparkSql = ("org.apache.spark" %% "spark-sql" % sparkV % "provided")
      .exclude("org.apache.logging.log4j", "log4j-slf4j-impl")
      .excludeAll("org.apache.hadoop")

    val hadoopAws = ("org.apache.hadoop" % "hadoop-aws" % hadoopV % "provided")
      .exclude("org.apache.logging.log4j", "log4j-slf4j-impl")
    val hadoopClient =  ("org.apache.hadoop" % "hadoop-client" % hadoopV % "provided")
      .exclude("org.apache.logging.log4j", "log4j-slf4j-impl")

    val iceberg = "org.apache.iceberg" % "iceberg-spark-runtime-3.3_2.12" % icebergV
    val nessie = "org.projectnessie" % "nessie-spark-extensions-3.1_2.12" % nessieV
    val glue = "software.amazon.awssdk" % "glue" % "2.17.257"
    val awsSdk = "software.amazon.awssdk" % "bundle" % "2.17.257"
    val urlConnectionClient = "software.amazon.awssdk" % "url-connection-client" % "2.17.257"
  }
}
