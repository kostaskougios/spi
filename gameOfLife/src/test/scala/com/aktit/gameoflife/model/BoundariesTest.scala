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
}
