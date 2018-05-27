package com.aktit.gameoflife

import com.aktit.gameoflife.spark.{CreateCommand, PlayCommand}
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
  * (delete it: kafka-topics.sh --zookeeper server.lan:2181 --delete --topic GameOfLifeCommands )
  *
  * Give commands from the console:
  *
  * kafka-console-producer.sh --broker-list server.lan:9092 --topic GameOfLifeCommands
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
					logInfo(s"Got ${rdd.count()} commands")

					val commands = rdd.flatMap {
						cr =>
							// but now on executor
							val commands = cr.value.split(" ").toList
							logInfo(s"Got $commands")
							commands match {
								case List("create", gameName, sectorWidth, sectorHeight, numSectorsHorizontal, numSectorsVertical, howManyLiveCells) =>
									Some(CreateCommand(gameName, sectorWidth.toInt, sectorHeight.toInt, numSectorsHorizontal.toInt, numSectorsVertical.toInt, howManyLiveCells.toInt))
								case List("play", gameName, turn) =>
									Some(PlayCommand(gameName, turn.toInt))
								case _ =>
									logWarning(s"Invalid command : $commands")
									None
							}
					}.collect

					// Update the offsets before executing the commands. This means if execution fails, the commands
					// won't be run again. This is done on purpose so that we can give an other command if we need to.
					val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
					messages.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)

					// execute the commands
					commands.foreach(_.run(rdd.sparkContext, out))
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}
}
