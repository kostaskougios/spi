package com.aktit.sql.performance

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession

import scala.util.Random

/**
  * Create random impression data to be used by the benchmarks.
  *
  * See bin/create-random-data
  *
  * Run locally with these params:
  *
  * -Dspark.master=local[4]
  * -Dspark.memory.fraction=0.3
  * -Dspark.memory.storageFraction=0.2
  * -Dspark.executor.heartbeatInterval=60s
  * -Dspark.creator.num-of-rows=2000000000
  * -Dspark.creator.target-dir=/tmp/test-data
  *
  * @author kostas.kougios
  *         10/07/18 - 09:43
  */
object CreateRandomData extends Logging
{
	val MaxUsers = 1000000
	val MaxProducts = 5000
	val Group = 5000000 // reduce this if you have less memory

	def main(args: Array[String]): Unit = {

		val spark = SparkSession.builder.config("spark.sql.orc.impl", "native").getOrCreate
		val conf = spark.conf
		val numOfRows = conf.get("spark.creator.num-of-rows").toLong
		val impressionsTargetDir = conf.get("spark.creator.target-dir")

		logInfo(s"Impressions: Will append ${numOfRows / Group} times")

		val startClock = Instant.parse("2010-01-01T00:00:00.00Z")

		new Creator(
			spark,
			for (i <- (1l to numOfRows).toIterator) yield
				PageImpression(
					Random.nextInt(MaxUsers),
					Timestamp.from(startClock.plusSeconds(i)),
					s"http://www.some-server.com/part1/part2/$i"
				),
			impressionsTargetDir + "/impressions",
			Group
		).create()

		new Creator(
			spark,
			for (i <- (1l to numOfRows).toIterator) yield {
				val productId = Random.nextInt(MaxProducts)
				val price = productId % 250
				val discount = Random.nextInt(30)
				Order(
					Random.nextInt(MaxUsers),
					UUID.randomUUID.toString,
					Timestamp.from(startClock.plusSeconds(i)),
					productId,
					s"product-code-for-$productId",
					s"product-title-for-$productId",
					price,
					price * (1.00f - discount.toFloat / 100),
					discount.toByte
				)
			},
			impressionsTargetDir + "/orders",
			Group
		).create()
	}
}
