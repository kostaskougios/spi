package com.aktit.kafka.serialization

import java.util

import com.aktit.loaders.dto.XmlRow
import org.apache.commons.lang3.SerializationUtils
import org.apache.kafka.common.serialization.Serializer

/**
  * @author kostas.kougios
  *         15/05/18 - 11:31
  */
class XmlRowSerializer extends Serializer[XmlRow]
{
	override def configure(map: util.Map[String, _], b: Boolean) = {}

	// just java serialization, good enough for an example
	override def serialize(s: String, t: XmlRow) = SerializationUtils.serialize(t)

	override def close() = {}
}
