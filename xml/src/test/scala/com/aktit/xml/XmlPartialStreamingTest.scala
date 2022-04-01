package com.aktit.xml

import org.apache.commons.io.IOUtils
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios
  */
class XmlPartialStreamingTest extends AnyFunSuite {
  val ps = new XmlPartialStreaming

	// @formatter:off
  test("parses") {
    val stream = ps.parse(IOUtils.toInputStream(
      """
        |<x>
        | <mark><t a="2">text1</t></mark>
        |
        | <mark><t>text2</t></mark>
        |</x>
      """.stripMargin,"UTF-8"), "mark")
    stream.toList should be(
      List(
        <mark><t a="2">text1</t></mark>
        ,
        <mark><t>text2</t></mark>
      )
    )
  }

  test("parses taking care of same deep elements") {
    val stream = ps.parse(IOUtils.toInputStream(
      """
        |<x>
        | <mark><t a="2"><mark>text1</mark></t></mark>
        |</x>
      """.stripMargin,"UTF-8"), "mark").toList
    stream should be(
      List(
        <mark><t a="2"><mark>text1</mark></t></mark>
      )
    )
  }

  test("unescaped attribute") {
    ps.parse(IOUtils.toInputStream(
      """
        |<x>
        | <mark><t a="2&amp;3"><mark>text1</mark></t></mark>
        |</x>
      """.stripMargin,"UTF-8"), "mark").toList should be(
      List(
        <mark><t a="2&amp;3"><mark>text1</mark></t></mark>
      )
    )
  }
}
