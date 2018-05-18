package com.aktit.kafka.serialization

import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.util

import com.aktit.wikipedia.dto.Page
import org.apache.kafka.common.serialization.Deserializer

/**
  * @author kostas.kougios
  *         15/05/18 - 15:36
  */
class PageDeserializer extends Deserializer[Page]
{
	override def configure(configs: util.Map[String, _], isKey: Boolean) = {}

	override def deserialize(topic: String, data: Array[Byte]) = {
		val oin = new ObjectInputStream(new ByteArrayInputStream(data))
		oin.readObject.asInstanceOf[Page]
	}

	override def close() = {}
}
