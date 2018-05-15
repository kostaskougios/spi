package com.aktit.kafka

import java.util.Properties

/**
  * Will break up the large wikipedia xml files and send each <page> element
  * to kafka.
  *
  * This needs to run locally on a machine with the files.
  *
  * @author kostas.kougios
  *         15/05/18 - 11:27
  */
object ConsumeWikipediaPages extends App
{
	val brokers = "server.lan"

	val props = new Properties()
	props.put("bootstrap.servers", brokers)
	props.put("client.id", "ConsumeWikipediaPages")
	props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
	props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

}
