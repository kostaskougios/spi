package com.aktit.kafka

import java.io.{File, FileInputStream}
import java.util.Properties

import com.aktit.kafka.serialization.PageSerializer
import com.aktit.wikipedia.dto.Page
import com.aktit.xml.XmlPartialStreaming
import org.apache.commons.io.FileUtils
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.LongSerializer

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

/**
  * Will break up the large wikipedia xml files and send each <page> element
  * to kafka.
  *
  * Create the topic via:
  *
  * (delete it first : kafka-topics.sh --zookeeper server.lan:2181 --delete --topic WikipediaPages )
  * kafka-topics.sh --create --zookeeper server.lan:2181 --replication-factor 1 --partitions 8 --topic WikipediaPages
  *
  * Then run this on a machine with the files.
  *
  * @author kostas.kougios
  *         15/05/18 - 11:27
  */
object WikipediaPagesProducer extends App
{
	val brokers = "server.lan:9092"
	val topic = "WikipediaPages"
	// the directory where the wikipedia xml files are.
	val srcDir = "/home/ariskk/big-data/wikipedia"

	val props = new Properties()
	props.put("bootstrap.servers", brokers)
	props.put("client.id", getClass.getSimpleName)
	props.put("key.serializer", classOf[LongSerializer])
	// We will store the data in kafka by serializing the Page class.
	props.put("value.serializer", classOf[PageSerializer])
	//props.put("batch.size", "16384")

	val producer = new KafkaProducer[Long, Page](props)
	val allFiles = FileUtils.listFiles(new File(srcDir), Array("xml"), true).asScala.toList
	XmlPartialStreaming.setup()

	for (file <- allFiles) {
		val xml = new XmlPartialStreaming
		for (n <- xml.parse(new FileInputStream(file), "page")) {
			val lang = file.getName.substring(0, 2)

			val page = Page.fromXml(n, lang)
			val record = new ProducerRecord[Long, Page](topic, page.id, page)
			producer.send(record)
		}
	}
}
