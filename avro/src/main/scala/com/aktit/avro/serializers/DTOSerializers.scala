package com.aktit.avro.serializers

import com.aktit.avro.AvroSerializer
import com.aktit.wikipedia.dto.Page

/**
  * @author kostas.kougios
  *         21/05/18 - 15:24
  */
object DTOSerializers
{
	// one instance is the more performance efficient due to the reuse of avro4s implicit instances

	val pageSerializer = new AvroSerializer[Page]

}
