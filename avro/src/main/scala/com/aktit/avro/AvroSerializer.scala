package com.aktit.avro

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.sksamuel.avro4s._

/**
  * @author kostas.kougios
  *         21/05/18 - 11:46
  */
class AvroSerializer[T: SchemaFor : Encoder : Decoder]
{
	private val schema = AvroSchema[T]

	def serializeSingleBinary(t: T): Array[Byte] = {
		val bos = new ByteArrayOutputStream(512)
		val aos = AvroOutputStream.binary[T].to(bos).build(schema)
		try aos.write(t) finally aos.close()
		bos.close()
		bos.toByteArray
	}

	def deserializeSingleBinary(a: Array[Byte]): T = {
		val in = new ByteArrayInputStream(a)
		val ais = AvroInputStream.binary.from(in).build(schema)
		try ais.iterator.next finally ais.close()
	}
}
