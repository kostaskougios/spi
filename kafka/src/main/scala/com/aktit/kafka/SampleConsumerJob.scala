package com.aktit.kafka

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._

/**
  * a spark streaming job that consumes a kafka stream every 2 seconds
  *
  * @author kostas.kougios
  */
object SampleConsumerJob
{
	def main(args: Array[String]): Unit = {
		val brokers = "d1.lan:9092,d2.lan:9092,d3.lan:9092"
		val topics = Set("test_topic")
		val kafkaParams = Map(
			"metadata.broker.list" -> brokers,
			"auto.offset.reset" -> "smallest"
		)

		val conf = new SparkConf().setAppName(getClass.getName)
		val ssc = new StreamingContext(conf, Seconds(2))
		try {

			// NOTE: not sure if the below is correct
			val messages = KafkaUtils.createDirectStream[String, String](
				ssc,
				LocationStrategies.PreferBrokers,
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
