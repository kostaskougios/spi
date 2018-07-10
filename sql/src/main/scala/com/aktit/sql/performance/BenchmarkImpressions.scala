package com.aktit.sql.performance

import com.aktit.utils.TimeMeasure
import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

/**
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object BenchmarkImpressions extends Logging
{

	def main(args: Array[String]): Unit = {

		val spark = SparkSession.builder.getOrCreate

		withCorrectSchema(spark).avro("/tmp/impressions/avro").createOrReplaceTempView("impressions_avro")
		withCorrectSchema(spark).parquet("/tmp/impressions/parquet").createOrReplaceTempView("impressions_parquet")

		val (avroImpressions, _) = TimeMeasure.dt(spark.sql("select count(*) from impressions_avro").show())
		val (parquetImpressions, _) = TimeMeasure.dt(spark.sql("select count(*) from impressions_parquet").show())

		val (avroDistinctUsers, _) = TimeMeasure.dt(spark.sql("select count(distinct userId) from impressions_avro").show())
		val (parquetDistinctUsers, _) = TimeMeasure.dt(spark.sql("select count(distinct userId) from impressions_parquet").show())

		logInfo(
			s"""
			   |Benchmark results.
			   |
			   |Benchmark / Avro / Parquet times (ms)
			   |
			   |select count(*) : $avroImpressions / $parquetImpressions
			   |select count(distinct userId) : $avroDistinctUsers / $parquetDistinctUsers
			""".stripMargin)
	}

	private def withCorrectSchema(spark: SparkSession) = spark.read.schema(
		StructType(Seq(
			StructField("userId", LongType, false),
			StructField("date", TimestampType, false),
			StructField("refererUrl", StringType, true)
		))
	)
}
