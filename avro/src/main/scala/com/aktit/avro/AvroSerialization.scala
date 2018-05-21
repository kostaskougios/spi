package com.aktit.avro

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.sksamuel.avro4s._

/**
  * @author kostas.kougios
  *         21/05/18 - 11:46
  */
object AvroSerialization
{
	def serializeSingleBinary[T: SchemaFor : ToRecord](t: T): Array[Byte] = {
		val bos = new ByteArrayOutputStream
		val aos = AvroOutputStream.binary[T](bos)
		try aos.write(t) finally aos.close()
		bos.close()
		bos.toByteArray
	}

	def deserializeSingleBinary[T: SchemaFor : FromRecord](a: Array[Byte]): T = {
		val ais = AvroInputStream.binary(new ByteArrayInputStream(a))
		try ais.iterator.next finally ais.close()
	}
}
