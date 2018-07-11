package com.aktit.sql.performance

import java.sql.Timestamp
import java.time.Instant

import com.databricks.spark.avro._
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

/**
  * Create random impression data to be used by the benchmarks.
  *
  * Takes 1h 15m on i7 4770k with SSD drive.
  *
  * 11G	/tmp/impressions/avro
  * 16G	/tmp/impressions/parquet
  * 27G	/tmp/impressions
  *
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object CreateImpressions extends Logging
{
	val TargetDir = "/tmp/impressions_large"
	val MaxUsers = 1000000
	val Impressions = 1000000000
	val Group = 5000000 // reduce this if you have less memory

	def main(args: Array[String]): Unit = {

		val startClock = Instant.parse("2010-01-01T00:00:00.00Z")

		def testData = for (i <- (1 to Impressions).toIterator) yield PageImpression(Random.nextInt(MaxUsers), Timestamp.from(startClock.plusSeconds(i)), s"http://www.some-server.com/part1/part2/$i")

		val spark = SparkSession.builder.config("spark.sql.orc.impl", "native").getOrCreate

		logInfo(s"Will append ${Impressions / Group} times")

		// We need to create a lot of test data without running out of memory. So we group the data together (using iterators to avoid filling up the memory)
		// and append them to our target directories
		for ((data, grp) <- testData.grouped(Group).zipWithIndex) {

			logInfo(s"parallelizing test data group $grp out of ${Impressions / Group}")

			val rdd = spark.sparkContext.parallelize(data)

			logInfo("Creating dataframe")
			val df = spark.createDataFrame(rdd).toDF

			logInfo(s"Schema : ${df.schema}")

			logInfo("Storing ORC")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).orc(s"$TargetDir/orc")

			logInfo("Storing Avro")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).avro(s"$TargetDir/avro")

			logInfo("Storing Parquet")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).parquet(s"$TargetDir/parquet")

		}
	}
}
