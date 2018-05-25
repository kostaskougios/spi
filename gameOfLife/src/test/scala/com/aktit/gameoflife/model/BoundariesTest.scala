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
		val live = Array(-1, 3, 7, 10)
		val b = boundaries(top = live)
		for (x <- -1 to 10) {
			b.isTop(x) should be(live.contains(x))
		}
	}

	test("isBottom") {
		val live = Array(-1, 3, 7, 10)
		val b = boundaries(bottom = live)
		for (x <- -1 to 10) {
			b.isBottom(x) should be(live.contains(x))
		}
	}

	test("isLeft") {
		val live = Array(0, 2, 4)
		val b = boundaries(left = live)
		for (y <- 0 to 5) {
			b.isLeftLive(y) should be(live.contains(y))
		}
	}

	test("isRight") {
		val live = Array(0, 2, 4)
		val b = boundaries(right = live)
		for (y <- 0 to 5) {
			b.isRightLive(y) should be(live.contains(y))
		}
	}

	test("out of bounds, top left") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(top = Array(-2))
		}
	}

	test("out of bounds, top right") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(top = Array(11))
		}
	}

	test("out of bounds, bottom left") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(bottom = Array(-2))
		}
	}

	test("out of bounds, bottom right") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(bottom = Array(11))
		}
	}

	test("out of bounds, left (top)") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(left = Array(-1))
		}
	}

	test("out of bounds, left (bottom)") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(left = Array(5))
		}
	}

	test("out of bounds, right (top)") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(right = Array(-1))
		}
	}

	test("out of bounds, right (bottom)") {
		an[IllegalArgumentException] should be thrownBy {
			boundaries(right = Array(5))
		}
	}
}
