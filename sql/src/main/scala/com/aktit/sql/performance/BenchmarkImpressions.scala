package com.aktit.sql.performance

import java.time.Instant

import com.aktit.utils.{Tabulator, TimeMeasure}
import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

/**
  * Benchmark the Impressions table, a table with a small number of columns
  *
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object BenchmarkImpressions extends Logging
{
	val SrcDir = "/tmp/impressions"

	def main(args: Array[String]): Unit = {

		val spark = SparkSession.builder.config("spark.sql.orc.impl", "native").getOrCreate
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

		spark.read.avro(s"$SrcDir/avro").createOrReplaceTempView("impressions_avro")
		spark.read.parquet(s"$SrcDir/parquet").createOrReplaceTempView("impressions_parquet")
		spark.read.orc(s"$SrcDir/orc").createOrReplaceTempView("impressions_orc")

		logInfo(
			"\n" +
				Tabulator.format(
					Seq(
						Seq("Query", "Avro", "Parquet", "ORC"),
						measureQuery(t => s"select count(distinct userId) as c,max(date),min(date),userId from impressions_$t group by userId order by c desc limit 5"),
						measureQuery(t => s"select count(distinct userId) as c,userId from impressions_$t group by userId order by c desc limit 5"),
						measureQuery(t => s"select count(*) from impressions_$t"),
						measureQuery(t => s"select count(distinct userId) from impressions_$t"),
						measureQuery(t => s"select min(date) from impressions_$t"),
						measureQuery(t => s"select max(date) from impressions_$t"),
						measureDateQuery("2010-02-01T00:00:00.00Z", "2010-03-01T00:00:00.00Z", (t, start, end) => s"select count(distinct userId) from impressions_$t where date between '$start' and '$end'"),
						measureDateQuery("2010-08-01T00:00:00.00Z", "2010-09-01T00:00:00.00Z", (t, start, end) => s"select count(distinct userId) from impressions_$t where date between '$start' and '$end'")
					)
				)
		)

	}

}
