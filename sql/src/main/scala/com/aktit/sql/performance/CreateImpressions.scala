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
	val Impressions = 1000000000
	val Group = 5000000

	def main(args: Array[String]): Unit = {

		val startClock = Instant.parse("2010-01-01T00:00:00.00Z")

		def testData = for (i <- (1 to Impressions).toIterator) yield PageImpression(Random.nextInt(MaxUsers), Timestamp.from(startClock.plusSeconds(i)), s"http://ref/$i")

		val spark = SparkSession.builder.getOrCreate

		logInfo(s"Will append ${Impressions / Group} times")

		for ((data, grp) <- testData.grouped(Group).zipWithIndex) {

			logInfo(s"parallelizing test data group $grp")

			val rdd = spark.sparkContext.parallelize(data)

			logInfo("Creating dataframe")
			val df = spark.createDataFrame(rdd).toDF

			logInfo(s"Schema : ${df.schema}")

			logInfo("Storing Avro")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).avro("/tmp/impressions/avro")

			logInfo("Storing Parquet")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).parquet("/tmp/impressions/parquet")
		}
	}
}
