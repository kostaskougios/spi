package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.boundaries
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         25/05/18 - 22:44
  */
class BoundariesTest extends FunSuite
{
	test("isTop") {
		val b = boundaries(top = Array(-1, 3, 7, 11))
		b.isTop(-1) should be(true)
		b.isTop(0) should be(false)
		b.isTop(3) should be(true)
		b.isTop(4) should be(false)
		b.isTop(10) should be(false)
		b.isTop(11) should be(true)
	}
}
