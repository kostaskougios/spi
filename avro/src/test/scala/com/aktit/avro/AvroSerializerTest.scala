package com.aktit.avro

import org.apache.commons.lang3.SerializationUtils
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios 05/06/18 - 20:46
  */
class AvroSerializerTest extends AnyFunSuite {

  val x = X(5, "hello")

  val serializer = new AvroSerializer[X]

  test("serialize/deserialize") {
    val data = serializer.serializeSingleBinary(x)
    serializer.deserializeSingleBinary(data) should be(x)
  }

  test("bytesize") {
    val avroSz = serializer.serializeSingleBinary(x).length
    val javaSz = SerializationUtils.serialize(x).length
    avroSz should be < javaSz

  }
}

case class X(i: Int, s: String)
