package com.aktit.kafka

import com.aktit.kafka.serialization.PageDeserializer
import com.aktit.wikipedia.dto.Page
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._

/**
  * Spark stream job that consumes ConsumeWikipediaPages periodically.
  *
  * @author kostas.kougios
  */
object WikipediaPagesConsumerJob
{
	def main(args: Array[String]): Unit = {
		val conf = new SparkConf().setAppName(getClass.getName)

		val topics = Set("WikipediaPages")

		val kafkaParams = Map[String, Object](
			"bootstrap.servers" -> conf.get("spark.bootstrap.servers"),
			"key.deserializer" -> classOf[LongDeserializer],
			"value.deserializer" -> classOf[PageDeserializer],
			"group.id" -> WikipediaPagesConsumerJob.getClass.getSimpleName,
			"auto.offset.reset" -> "latest",
			"enable.auto.commit" -> (false: java.lang.Boolean)
		)

		val ssc = new StreamingContext(conf, Seconds(2))
		try {

			// NOTE: not sure if the below is correct
			val messages = KafkaUtils.createDirectStream(
				ssc,
				LocationStrategies.PreferConsistent,
				ConsumerStrategies.Subscribe[Long, Page](topics, kafkaParams)
			)
			messages.foreachRDD {
				rdd =>
					rdd.foreach {
						record =>
							val page = record.value
							println(s"--------------------> ${Thread.currentThread} ${page.lang} - ${page.title}")
					}
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}
}
