package com.aktit.kafka.serialization

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.util

import com.aktit.wikipedia.dto.Page
import org.apache.kafka.common.serialization.Serializer

/**
  * @author kostas.kougios
  *         15/05/18 - 11:31
  */
class PageSerializer extends Serializer[Page]
{
	override def configure(map: util.Map[String, _], b: Boolean) = {}

	// just java serialization, good enough for an example
	override def serialize(topic: String, page: Page) = {
		val bos = new ByteArrayOutputStream
		// TODO: use kryo or avro
		val oos = new ObjectOutputStream(bos)
		oos.writeObject(page)
		oos.close()
		bos.toByteArray
	}

	override def close() = {}
}
