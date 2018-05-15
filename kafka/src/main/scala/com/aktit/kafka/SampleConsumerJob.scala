package com.aktit.kafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._

/**
  * A spark streaming job that consumes a kafka stream every 2 seconds.
  *
  * Create the topic via:
  *
  * kafka-topics.sh --create --zookeeper server.lan:2181 --replication-factor 1 --partitions 1 --topic test_spark
  *
  * @author kostas.kougios
  */
object SampleConsumerJob
{
	def main(args: Array[String]): Unit = {
		val topics = Set("test_spark")

		val kafkaParams = Map[String, Object](
			"bootstrap.servers" -> "server.lan:9092",
			"key.deserializer" -> classOf[StringDeserializer],
			"value.deserializer" -> classOf[StringDeserializer],
			"group.id" -> WikipediaPagesConsumerJob.getClass.getSimpleName,
			"auto.offset.reset" -> "latest",
			"enable.auto.commit" -> (false: java.lang.Boolean)
		)

		val conf = new SparkConf().setAppName(getClass.getName)
		val ssc = new StreamingContext(conf, Seconds(2))
		try {

			// NOTE: not sure if the below is correct
			val messages = KafkaUtils.createDirectStream[String, String](
				ssc,
				LocationStrategies.PreferConsistent,
				ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
			)
			messages.foreachRDD {
				rdd =>
					rdd.foreach {
						cr =>
							println(s"--------------------> ${Thread.currentThread} ${cr.key} - ${cr.value}")
					}
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}
}
