package com.aktit.kafka.serialization

import java.util

import com.aktit.wikipedia.dto.Page
import org.apache.commons.lang3.SerializationUtils
import org.apache.kafka.common.serialization.Serializer

/**
  * @author kostas.kougios
  *         15/05/18 - 11:31
  */
class PageSerializer extends Serializer[Page]
{
	override def configure(map: util.Map[String, _], b: Boolean) = {}

	// just java serialization, good enough for an example, TODO: use kryo or avro
	override def serialize(topic: String, page: Page) = SerializationUtils.serialize(page)

	override def close() = {}
}
