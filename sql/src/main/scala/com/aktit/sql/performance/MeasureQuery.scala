package com.aktit.sql.performance

import java.time.Instant

import com.aktit.utils.TimeMeasure
import org.apache.spark.sql.SparkSession

/**
  * @author kostas.kougios
  *         13/07/18 - 09:12
  */
class MeasureQuery(spark: SparkSession)
{

	import spark.sql

	def measureQuery(q: String => String) = {
		val (avro, _) = TimeMeasure.dt(sql(q("avro")).show())
		val (parquet, _) = TimeMeasure.dt(sql(q("parquet")).show())
		val (orc, _) = TimeMeasure.dt(sql(q("orc")).show())
		Seq(q("*"), avro, parquet, orc)
	}

	def measureDateQuery(startDate: String, endDate: String, q: (String, String, String) => String) = {
		// avro converts timestamps to Long and we have to query it based on long values
		val fromInstance = Instant.parse(startDate).toEpochMilli
		val toInstance = Instant.parse(endDate).toEpochMilli

		val (avro, _) = TimeMeasure.dt(sql(q("avro", fromInstance.toString, toInstance.toString)).show())
		val (parquet, _) = TimeMeasure.dt(sql(q("parquet", startDate, endDate)).show())
		val (orc, _) = TimeMeasure.dt(sql(q("orc", startDate, endDate)).show())
		Seq(q("*", startDate, endDate), avro, parquet, orc)
	}
}
