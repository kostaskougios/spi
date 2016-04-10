package com.aktit.kafka

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

			val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
			messages.foreachRDD {
				rdd =>
					rdd.foreach {
						case (s1, s2) =>
							println(s"--------------------> ${Thread.currentThread} $s1 - $s2")
					}
			}
			ssc.start()
			ssc.awaitTermination()
		} finally {
			ssc.stop()
		}
	}
}
