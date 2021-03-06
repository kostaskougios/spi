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
  * Get kafka group details (current offset etc) :
  *
  * kafka-consumer-groups.sh --bootstrap-server server.lan:9092 --describe --group WikipediaPagesConsumerJob
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
					// now break the pages to words and store them in cassandra for this batch
					rdd.flatMap {
						cr =>
							val page = cr.value
							createRows(page)
					}.saveToCassandra("wikipedia", "words", SomeColumns("word", "page_id", "revision_id"))
					// The consumer offsets won't automatically be stored. We need to update
					// them here because we consumed some data.
					// See https://spark.apache.org/docs/2.3.0/streaming-kafka-0-10-integration.html#storing-offsets
					// regarding other options for storing the kafka offsets.
					// saveToCassandra is idempotent, it is ok if we replay it more than once if
					// the code crashes before we store the offsets.
					val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
					messages.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}

	private def createRows(page: Page): Seq[(String, Long, Long)] = {
		page.revisions.flatMap {
			revision =>
				revision.breakToWords
					.distinct
					.map {
						word =>
							(StringUtils.substring(word, 0, 1024), page.id, revision.id)
					}
		}
	}
}
