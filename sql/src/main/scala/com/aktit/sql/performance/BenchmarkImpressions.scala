package com.aktit.sql.performance

import java.time.Instant

import com.aktit.utils.{Tabulator, TimeMeasure}
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

		val spark = SparkSession.builder.config("spark.sql.orc.impl", "native").getOrCreate

		def measureQuery(q: String => String) = {
			val (avro, _) = TimeMeasure.dt(spark.sql(q("avro")).show())
			val (parquet, _) = TimeMeasure.dt(spark.sql(q("parquet")).show())
			val (orc, _) = TimeMeasure.dt(spark.sql(q("orc")).show())
			(avro, parquet, orc)
		}

		def measureDateQuery(startDate: String, endDate: String, q: (String, String, String) => String) = {
			// avro converts timestamps to Long and we have to query it based on long values
			val fromInstance = Instant.parse(startDate).toEpochMilli
			val toInstance = Instant.parse(endDate).toEpochMilli

			val (avro, _) = TimeMeasure.dt(spark.sql(q("avro", fromInstance.toString, toInstance.toString)).show())
			val (parquet, _) = TimeMeasure.dt(spark.sql(q("parquet", startDate, endDate)).show())
			val (orc, _) = TimeMeasure.dt(spark.sql(q("orc", startDate, endDate)).show())
			(avro, parquet, orc)

		}

		spark.read.avro("/tmp/impressions/avro").createOrReplaceTempView("impressions_avro")
		spark.read.parquet("/tmp/impressions/parquet").createOrReplaceTempView("impressions_parquet")
		spark.read.orc("/tmp/impressions/orc").createOrReplaceTempView("impressions_orc")

		val (avroDistinctForDateUsers, parquetDistinctForDateUsers, orcDistinctForDateUsers) =
			measureDateQuery("2010-02-01T00:00:00.00Z", "2010-03-01T00:00:00.00Z", (t, start, end) => s"select count(distinct userId) from impressions_$t where date between '$start' and '$end'")

		val (avroImpressions, parquetImpressions, orcImpressions) = measureQuery(t => s"select count(*) from impressions_$t")
		val (avroDistinctUsers, parquetDistinctUsers, orcDistinctUsers) = measureQuery(t => s"select count(distinct userId) from impressions_$t")


		logInfo(
			"\n" +
				Tabulator.format(
					Seq(
						Seq("Query", "Avro", "Parquet", "ORC"),
						Seq("select count(*)", avroImpressions, parquetImpressions, orcImpressions),
						Seq("select count(distinct userId)", avroDistinctUsers, parquetDistinctUsers, orcDistinctUsers),
						Seq("select count(distinct userId) from impressions_avro where date between", avroDistinctForDateUsers, parquetDistinctForDateUsers, orcDistinctForDateUsers)
					)
				)
		)

	}

}
