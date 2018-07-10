package com.aktit.sql.performance

import java.sql.Timestamp
import java.time.Instant

import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

/**
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object CreateImpressions extends Logging
{
	val MaxUsers = 1000000

	def main(args: Array[String]): Unit = {

		val startClock = Instant.parse("2010-01-01T00:00:00.00Z")

		logInfo("Creating users")
		val testData = for (i <- 1 to 1000000) yield PageImpression(Random.nextInt(MaxUsers), Timestamp.from(startClock.plusSeconds(i)), s"http://ref/$i")

		logInfo("Storing users")

		val spark = SparkSession.builder.getOrCreate
		val df = spark.createDataFrame(testData).toDF("user", "date", "referer")

		println(df.schema)
		df.show()

		df.toDF.write.mode(SaveMode.Overwrite).avro("/tmp/impressions")
	}
}
