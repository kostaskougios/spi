package com.aktit.sql.performance

import java.sql.Timestamp
import java.time.Instant

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

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
	val MaxUsers = 1000000
	val Group = 5000000 // reduce this if you have less memory

	def main(args: Array[String]): Unit = {

		val spark = SparkSession.builder.config("spark.sql.orc.impl", "native").getOrCreate
		val conf = spark.conf
		val impressions = conf.get("spark.creator.impressions.num").toLong
		val impressionsTargetDir = conf.get("spark.creator.impressions.target-dir")

		logInfo(s"Impressions: Will append ${impressions / Group} times")

		val startClock = Instant.parse("2010-01-01T00:00:00.00Z")
		val testData = for (i <- (1l to impressions).toIterator)
			yield PageImpression(Random.nextInt(MaxUsers), Timestamp.from(startClock.plusSeconds(i)), s"http://www.some-server.com/part1/part2/$i")

		new Creator(
			spark,
			testData,
			impressionsTargetDir + "/impressions",
			Group
		).create()
	}
}
