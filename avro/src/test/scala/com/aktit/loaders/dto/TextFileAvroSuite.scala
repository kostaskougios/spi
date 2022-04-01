package com.aktit.loaders.dto

import com.aktit.avro.AvroSerializer
import com.aktit.loaders.dto.LoaderBuilders.textFile
import org.apache.commons.lang3.SerializationUtils
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios 21/05/18 - 11:25
  */
class TextFileAvroSuite extends AnyFunSuite {
  val avroSerializer = new AvroSerializer[TextFile]
  val tf = textFile(name = "avro test")

  test("serialize/deserialize") {
    val data = avroSerializer.serializeSingleBinary(tf)
    avroSerializer.deserializeSingleBinary(data) should be(tf)
  }

  // just to prove of the benefits ... (not a real test case)
  test("avro is more efficient than java serialization") {
    val jData = SerializationUtils.serialize(tf) // normally 125 bytes
    val aData = avroSerializer.serializeSingleBinary(tf) // should be 27 bytes
    aData.length should be < jData.length
  }
}
