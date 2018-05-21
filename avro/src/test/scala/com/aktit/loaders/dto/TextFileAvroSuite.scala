package com.aktit.loaders.dto

import com.aktit.avro.AvroSerialization
import com.aktit.loaders.dto.LoaderBuilders.textFile
import org.apache.commons.lang3.SerializationUtils
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         21/05/18 - 11:25
  */
class TextFileAvroSuite extends FunSuite
{
	val tf = textFile(name = "avro test")

	test("serialize/deserialize") {
		val data = AvroSerialization.serializeSingleBinary(tf)
		AvroSerialization.deserializeSingleBinary[TextFile](data) should be(tf)
	}

	// just to prove of the benefits ... (not a real test case)
	test("avro is more efficient than java serialization") {
		val jData = SerializationUtils.serialize(tf) // normally 125 bytes
		val aData = AvroSerialization.serializeSingleBinary(tf) // should be 27 bytes
		aData.length should be < jData.length
	}
}
