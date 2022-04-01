package com.aktit.sql.performance

import com.aktit.utils.Tabulator
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

/** Benchmark the Impressions table, a table with a small number of columns
  *
  * @author
  *   kostas.kougios 10/07/18 - 09:43
  */
object BenchmarkImpressions extends Logging {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().config("spark.sql.orc.impl", "native").getOrCreate()
    val srcDir = spark.conf.get("spark.src")

    val q = new MeasureQuery(spark)

    spark.read.format("avro").load(s"$srcDir/avro").createOrReplaceTempView("impressions_avro")
    spark.read.parquet(s"$srcDir/parquet").createOrReplaceTempView("impressions_parquet")
    spark.read.orc(s"$srcDir/orc").createOrReplaceTempView("impressions_orc")

    // warm up
    q.measureQuery(t => s"select * from impressions_$t limit 1000")

    logInfo(
      "\n" +
        Tabulator.format(
          Seq(
            Seq("Query", "Avro", "Parquet", "ORC"),
            q.measureQuery(t => s"select * from impressions_$t where userId=500000"),
            q.measureQuery(t => s"select count(userId) as c,max(date),min(date),userId from impressions_$t group by userId order by c desc limit 5"),
            q.measureQuery(t => s"select count(userId) as c,userId from impressions_$t group by userId order by c desc limit 5"),
            q.measureQuery(t => s"select count(*) from impressions_$t"),
            q.measureQuery(t => s"select count(distinct userId) from impressions_$t"),
            q.measureQuery(t => s"select min(date) from impressions_$t"),
            q.measureQuery(t => s"select max(date) from impressions_$t"),
            q.measureDateQuery(
              "2010-02-01T00:00:00.00Z",
              "2010-03-01T00:00:00.00Z",
              (t, start, end) => s"select count(distinct userId) from impressions_$t where date between '$start' and '$end'"
            ),
            q.measureDateQuery(
              "2010-08-01T00:00:00.00Z",
              "2010-09-01T00:00:00.00Z",
              (t, start, end) => s"select count(distinct userId) from impressions_$t where date between '$start' and '$end'"
            )
          )
        )
    )

  }

}
