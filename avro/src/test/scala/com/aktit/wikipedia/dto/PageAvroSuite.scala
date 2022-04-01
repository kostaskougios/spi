package com.aktit.wikipedia.dto

import com.aktit.avro._
import com.aktit.wikipedia.dto.WikipediaBuilder.page
import com.sksamuel.avro4s.FromRecord
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios 21/05/18 - 12:01
  */
class PageAvroSuite extends AnyFunSuite {
  val avroSerializer = new AvroSerializer[Page]
  val pg = page()

  implicit val fromRecord = FromRecord[Revision]

  test("serialization/deserialization") {
    val data = avroSerializer.serializeSingleBinary(pg)
    avroSerializer.deserializeSingleBinary(data) should be(pg)
  }
}
