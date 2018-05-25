package com.aktit.kafka

import com.aktit.kafka.serialization.PageDeserializer
import com.aktit.wikipedia.dto.Page
import com.datastax.spark.connector._
import org.apache.commons.lang3.StringUtils
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
/**
  * Spark stream job that consumes ConsumeWikipediaPages periodically.
  *
  * Before running, create the wikipedia keyspace and table via cqlsh:
  *
  * create keyspace wikipedia WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
  * create table wikipedia.words(word text, page_id int, revision_id int, primary key (word , page_id, revision_id));
  *
  * Run it via bin/kafka-wikipedia-pages-consumer-job
  *
  * Locally run with
  *
  * -Dspark.bootstrap.servers=server.lan:9092
  * -Dspark.master=local[4]
  * -Dspark.cassandra.connection.host=server.lan
  *
  * Get kafka group details :
  *
  * kafka-consumer-groups.sh --bootstrap-server server.lan:9092 --describe --group WikipediaPagesConsumerJob --members
  *
  * @author kostas.kougios
  */
object WikipediaPagesConsumerJob extends Logging
{
	def main(args: Array[String]): Unit = {
		val conf = new SparkConf().setAppName(getClass.getName)

		val topics = Set("WikipediaPages")

		val kafkaParams = Map[String, Object](
			"bootstrap.servers" -> conf.get("spark.bootstrap.servers"),
			"key.deserializer" -> classOf[LongDeserializer],
			"value.deserializer" -> classOf[PageDeserializer],
			"group.id" -> "WikipediaPagesConsumerJob",
			"auto.offset.reset" -> "earliest",
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
					// note that rdd.count actually forces the rdd to be calculated but it is useful stat during experimentation
					logInfo(s"Saving words from ${rdd.count} pages to cassandra")

					// now break the pages to words and store them in cassandra for this batch
					rdd.flatMap {
						cr =>
							val page = cr.value
							page.revisions.flatMap {
								revision =>
									revision.breakToWords.map {
										word =>
											(StringUtils.substring(word, 0, 1024), page.id, revision.id)
									}
							}
					}.saveToCassandra("wikipedia", "words", SomeColumns("word", "page_id", "revision_id"))
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}
}
