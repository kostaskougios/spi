package com.aktit.sql.performance

import com.aktit.utils.Tabulator
import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

/**
  * Benchmark the Impressions table, a table with a small number of columns
  *
  * Run with
  * -Dspark.master=local[4]
  * -Dspark.src=/tmp/big-data/orders
  *
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object BenchmarkOrders extends Logging
{
	def main(args: Array[String]): Unit = {

		val spark = SparkSession.builder.config("spark.sql.orc.impl", "native").getOrCreate
		val srcDir = spark.conf.get("spark.src")

		val q = new MeasureQuery(spark)

		spark.read.avro(s"$srcDir/avro").createOrReplaceTempView("orders_avro")
		spark.read.parquet(s"$srcDir/parquet").createOrReplaceTempView("orders_parquet")
		spark.read.orc(s"$srcDir/orc").createOrReplaceTempView("orders_orc")

		// warm up
		q.measureQuery(t => s"select * from orders_$t limit 1000")

		logInfo(
			"\n" +
				Tabulator.format(
					Seq(
						Seq("Query", "Avro", "Parquet", "ORC"),
						q.measureQuery(t => s"select sum(boughtPrice) s,userId from orders_$t group by userId order by s desc limit 10"),
						q.measureDateQuery("2010-01-01T00:00:00.00Z", "2010-03-01T00:00:00.00Z", (t, start, end) => s"select productCode,productTitle,max(discountPercentageApplied) d from orders_$t where date between '$start' and '$end' group by productCode,productTitle order by d desc limit 5"),
						q.measureDateQuery("2010-03-01T00:00:00.00Z", "2010-06-01T00:00:00.00Z", (t, start, end) => s"select productCode,productTitle,max(discountPercentageApplied) d from orders_$t where date between '$start' and '$end' group by productCode,productTitle order by d desc limit 5"),
						q.measureQuery(t => s"select productCode,productTitle,max(discountPercentageApplied) d from orders_$t group by productCode,productTitle order by d desc limit 5"),
						q.measureQuery(t => s"select * from orders_$t where userId=500000"),
						q.measureQuery(t => s"select * from orders_$t order by productId")
					)
				)
		)

	}

}
