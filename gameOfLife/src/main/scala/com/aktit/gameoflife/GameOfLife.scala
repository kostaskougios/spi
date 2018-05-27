package com.aktit.gameoflife

import com.aktit.gameoflife.spark.CreateCmd
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * This spark stream accepts game commands by listening to GameOfLifeCommands kafka topic.
  *
  * Create the topic:
  *
  * kafka-topics.sh --create --zookeeper server.lan:2181 --replication-factor 1 --partitions 1 --topic GameOfLifeCommands
  *
  * @author kostas.kougios
  *         27/05/18 - 20:06
  */
object GameOfLife extends Logging
{
	def main(args: Array[String]): Unit = {
		val conf = new SparkConf().setAppName(getClass.getName)

		val out = conf.get("spark.out")

		val kafkaParams = Map[String, Object](
			"bootstrap.servers" -> conf.get("spark.bootstrap.servers"),
			"key.deserializer" -> classOf[org.apache.kafka.common.serialization.StringDeserializer],
			"value.deserializer" -> classOf[org.apache.kafka.common.serialization.StringDeserializer],
			"group.id" -> "GameOfLife",
			"auto.offset.reset" -> "latest",
			"enable.auto.commit" -> (false: java.lang.Boolean)
		)

		val ssc = new StreamingContext(conf, Seconds(2))

		try {
			// NOTE: not sure if the below is correct
			val messages = KafkaUtils.createDirectStream(
				ssc,
				LocationStrategies.PreferConsistent,
				ConsumerStrategies.Subscribe[String, String](Set("GameOfLifeCommands"), kafkaParams)
			)
			messages.foreachRDD {
				rdd =>
					// still on driver
					rdd.flatMap {
						cr =>
							// but now on executor
							val commands = cr.value.split(" ").toList
							commands match {
								case List("create", gameName, sectorWidth, sectorHeight, numSectorsHorizontal, numSectorsVertical, howManyLiveCells) =>
									Some(CreateCmd(gameName, sectorWidth.toInt, sectorHeight.toInt, numSectorsHorizontal.toInt, numSectorsVertical.toInt, howManyLiveCells.toInt))
								case _ =>
									logWarning(s"Invalid command : $commands")
									None
							}
					}.collect
						.foreach(_.run(rdd.sparkContext, out))

					val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
					messages.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}
}
