package com.aktit.sql.performance

import java.time.Instant

import com.aktit.utils.TimeMeasure
import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

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

		// avro converts timestamps to Long and we have to query it based on long values
		val fromInstance = Instant.parse("2010-01-01T00:00:00.00Z").toEpochMilli
		val toInstance = Instant.parse("2010-02-01T00:00:00.00Z").toEpochMilli

		val (avroDistinctForDateUsers, _) = TimeMeasure.dt(spark.sql(s"select count(distinct userId) from impressions_avro where date between $fromInstance and $toInstance").show())
		val (parquetDistinctForDateUsers, _) = TimeMeasure.dt(spark.sql("select count(distinct userId) from impressions_parquet where date between '2010-01-01T00:00:00.00Z' and '2010-02-01T00:00:00.00Z'").show())

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
			   |select count(distinct userId) from impressions_avro where date between : $avroDistinctForDateUsers/$parquetDistinctForDateUsers
			""".stripMargin)
	}

	private def withCorrectSchema(spark: SparkSession) = spark.read

	//		.schema(
	//		StructType(Seq(
	//			StructField("userId", LongType, false),
	//			StructField("date", TimestampType, false),
	//			StructField("refererUrl", StringType, true)
	//		))
	//	)
}
