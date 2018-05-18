package com.aktit.kafka.serialization

import com.aktit.wikipedia.dto.WikipediaBuilder.page
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         18/05/18 - 18:42
  */
class PageSerializerTest extends FunSuite
{
	test("serialize / deserialize") {
		val p = page()
		val serialized = new PageSerializer().serialize("", p)
		val deserialized = new PageDeserializer().deserialize("", serialized)
		p should be(deserialized)
	}
}
