package com.aktit.kafka.serialization

import java.util

import com.aktit.avro.AvroSerialization
import com.aktit.wikipedia.dto.Page
import org.apache.kafka.common.serialization.Serializer

/**
  * @author kostas.kougios
  *         15/05/18 - 11:31
  */
class PageSerializer extends Serializer[Page]
{
	override def configure(map: util.Map[String, _], b: Boolean) = {}

	override def serialize(topic: String, page: Page) = AvroSerialization.serializeSingleBinary(page)

	override def close() = {}
}
