package com.aktit.avro.benchmarks

import com.aktit.avro.AvroSerialization
import com.aktit.wikipedia.dto.WikipediaBuilder.page
import org.apache.commons.lang3.SerializationUtils

/**
  * @author kostas.kougios
  *         21/05/18 - 14:54
  */
object AvroBenchmark extends App
{
	val pg = page()

	measure("java serialization", SerializationUtils.serialize(pg))
	measure("avro serialization", AvroSerialization.serializeSingleBinary(pg))

	def measure(title: String, f: => AnyRef) = {

		// warm up
		for (i <- 1 to 10000) f

		// benchmark
		val start = System.currentTimeMillis
		for (i <- 1 to 100000) f
		val stop = System.currentTimeMillis
		println(s"$title dt = ${stop - start}")
	}

}
