package com.aktit.avro

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.sksamuel.avro4s._

/**
  * Note: The class requires implicitly the schema,toRecord and fromRecord and passes those to the avro4s
  * methods explicitly. This is for performance reasons, otherwise avro4s macros will create and pass the implicits
  * for each call making the serialization very slow.
  *
  * @author kostas.kougios
  *         21/05/18 - 11:46
  */
class AvroSerializer[T: SchemaFor : ToRecord : FromRecord]
{
	def serializeSingleBinary(t: T): Array[Byte] = {
		val bos = new ByteArrayOutputStream(512)
		val aos = AvroOutputStream.binary[T](bos)
		try aos.write(t) finally aos.close()
		bos.close()
		bos.toByteArray
	}

	def deserializeSingleBinary(a: Array[Byte]): T = {
		val ais = AvroInputStream.binary(new ByteArrayInputStream(a))
		try ais.iterator.next finally ais.close()
	}
}
