package me.xsight.productengine.spark.runtime

import org.apache.spark.sql.SparkSession
import zio._


object Application {
  val catalogName1 = "multi_org_1_catalog"
  val catalogName2 = "multi_org_2_catalog"
  val s3RootOrg1 = "s3://test-bucket-serhii/multi_org_1"
  val org1Db = "dv_multi_org"
  val org2Db = "dv_multi_org_2"
  val org1Table = "table_org_1"
  val org2Table = "table_org_2"
  val s3RootOrg2 = "s3://test-bucket-serhii/multi_org2"
  val app = ZIO.scoped {
    getSparkSession.flatMap { spark =>
      readFromTwoCatalogsAndJoin(spark)
    }
  }

  private def readFromTwoCatalogsAndJoin(spark: SparkSession): Task[Unit] = ZIO.attempt {
    // Set catalog configurations for Catalog 1
    spark.conf.set(s"spark.sql.catalog.$catalogName1", "org.apache.iceberg.spark.SparkCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName1.catalog-impl", "org.apache.iceberg.aws.glue.GlueCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName1.warehouse", s3RootOrg1)
    spark.conf.set(s"spark.sql.catalog.$catalogName1.io-impl", "org.apache.iceberg.aws.s3.S3FileIO")
    spark.conf.set("spark.sql.catalog.glue.glue.skip-name-validation", true)

    // Read DataFrame 1 from Catalog 1
//    val df1 = spark.read
//      .format("iceberg")
//      .load(s"$catalogName1.$org1Db.$org1Table")

    spark.sql(s"USE $catalogName1.$org1Db")
    val df1 = spark.sql(s"SELECT * FROM $org1Table")

    // Set catalog configurations for Catalog 2
    spark.conf.set(s"spark.sql.catalog.$catalogName2", "org.apache.iceberg.spark.SparkCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName2.catalog-impl", "org.apache.iceberg.aws.glue.GlueCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName2.warehouse", s3RootOrg2)
    spark.conf.set(s"spark.sql.catalog.$catalogName2.io-impl", "org.apache.iceberg.aws.s3.S3FileIO")
    spark.conf.set("spark.sql.catalog.glue.glue.skip-name-validation", true)

    // Read DataFrame 2 from Catalog 2
//    val df2 = spark.read
//      .format("iceberg")
//      .load(s"$catalogName2.$org2Db.$org2Table")

    spark.sql(s"USE $catalogName2.$org2Db")
    val df2 = spark.sql(s"SELECT * FROM $org2Table")

    // Join the two DataFrames on the "id" column
    val joinedDf = df1.join(df2, "id")

    joinedDf.show(10)
  }

  private def writeToTwoCatalogs(spark: SparkSession): Task[Unit] = ZIO.attempt {
    // Define the DataFrames
    val df1 = spark.createDataFrame(Seq((0, "John", 25), (1, "Jane", 30))).toDF("id", "name", "age")
    val df2 = spark.createDataFrame(Seq((0, 56), (1, 70))).toDF("id", "weight")
    // Set catalog configurations for Catalog 1
    spark.conf.set(s"spark.sql.catalog.$catalogName1", "org.apache.iceberg.spark.SparkCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName1.catalog-impl", "org.apache.iceberg.aws.glue.GlueCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName1.warehouse", s3RootOrg1)
    spark.conf.set(s"spark.sql.catalog.$catalogName1.io-impl", "org.apache.iceberg.aws.s3.S3FileIO")
    spark.conf.set("spark.sql.catalog.glue.glue.skip-name-validation", true)

    // Write DataFrame 1 to Catalog 1 as Iceberg format
    df1.createOrReplaceTempView("tmp_table_org_1")
    spark.sql(
      s"""CREATE TABLE $catalogName1.$org1Db.$org1Table
        USING iceberg
        AS SELECT * FROM tmp_table_org_1""")

    // Set catalog configurations for Catalog 2
    spark.conf.set(s"spark.sql.catalog.$catalogName2", "org.apache.iceberg.spark.SparkCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName2.catalog-impl", "org.apache.iceberg.aws.glue.GlueCatalog")
    spark.conf.set(s"spark.sql.catalog.$catalogName2.warehouse", s3RootOrg2)
    spark.conf.set(s"spark.sql.catalog.$catalogName2.io-impl", "org.apache.iceberg.aws.s3.S3FileIO")
    spark.conf.set("spark.sql.catalog.glue.glue.skip-name-validation", true)

    // Write DataFrame 2 to Catalog 2 as Iceberg format
    df2.createOrReplaceTempView("tmp_table_org_2")
    spark.sql(
      s"""CREATE TABLE $catalogName2.$org2Db.$org2Table
        USING iceberg
        AS SELECT * FROM tmp_table_org_2""")
  }.unit

  private def getSparkSession: ZIO[Scope, Throwable, SparkSession] = ZIO.attempt(
    SparkSession.builder()
      .appName("Write to Glue Catalog")
      .getOrCreate()
  ).withFinalizer(session => ZIO.attempt(session.stop()).orDie)

}
