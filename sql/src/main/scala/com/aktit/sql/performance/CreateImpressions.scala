package com.aktit.sql.performance

import java.sql.Timestamp
import java.time.Instant

import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object CreateImpressions extends Logging
{
	def main(args: Array[String]): Unit = {
		val spark = SparkSession.builder.getOrCreate
		val df = spark.createDataFrame(
			Seq(
				PageImpression("user1", Timestamp.from(Instant.now), "http://ref1")
			)
		).toDF("user", "date", "referer")

		println(df.schema)
		df.show()

		df.toDF.write.mode(SaveMode.Overwrite).avro("/tmp/output")
	}
}
