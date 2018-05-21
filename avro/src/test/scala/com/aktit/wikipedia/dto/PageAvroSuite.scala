package com.aktit.wikipedia.dto

import com.aktit.avro._
import com.aktit.wikipedia.dto.WikipediaBuilder.page
import com.sksamuel.avro4s.FromRecord
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         21/05/18 - 12:01
  */
class PageAvroSuite extends FunSuite
{
	val pg = page()

	implicit val fromRecord = FromRecord[Revision]

	test("serialization/deserialization") {
		val data = AvroSerialization.serializeSingleBinary(pg)
		AvroSerialization.deserializeSingleBinary[Page](data) should be(pg)
	}
}
