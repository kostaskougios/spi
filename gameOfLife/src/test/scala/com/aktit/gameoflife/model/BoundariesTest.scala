package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.{boundaries, universe}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

import scala.collection.immutable.BitSet

/** @author
  *   kostas.kougios 25/05/18 - 22:44
  */
class BoundariesTest extends AnyFunSuite {
  test("isTop") {
    val live = Array(-1, 3, 7, 10)
    val b = boundaries(top = live)
    for (x <- -1 to 10) {
      b.isTopLive(x) should be(live.contains(x))
    }
  }

  test("isBottom") {
    val live = Array(-1, 3, 7, 10)
    val b = boundaries(bottom = live)
    for (x <- -1 to 10) {
      b.isBottomLive(x) should be(live.contains(x))
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

  test("fromEdges, top") {
    val b = Boundaries.fromEdges(
      universe(),
      Seq(
        Corner.bottomRight(true),
        Corner.bottomLeft(true),
        Side.bottom(BitSet(0, 2))
      )
    )
    b.isTopLive(-1) should be(true)
    b.isTopLive(0) should be(true)
    b.isTopLive(1) should be(false)
    b.isTopLive(2) should be(true)
    b.isTopLive(3) should be(false)
    b.isTopLive(10) should be(true)
  }

  test("fromEdges, bottom") {
    val b = Boundaries.fromEdges(
      universe(),
      Seq(
        Corner.topRight(true),
        Corner.topLeft(true),
        Side.top(BitSet(0, 2))
      )
    )
    b.isBottomLive(-1) should be(true)
    b.isBottomLive(0) should be(true)
    b.isBottomLive(1) should be(false)
    b.isBottomLive(2) should be(true)
    b.isBottomLive(3) should be(false)
    b.isBottomLive(10) should be(true)
  }

  test("fromEdges, right") {
    val b = Boundaries.fromEdges(
      universe(),
      Seq(
        Side.left(BitSet(0, 2, 4))
      )
    )
    b.isRightLive(0) should be(true)
    b.isRightLive(1) should be(false)
    b.isRightLive(2) should be(true)
    b.isRightLive(3) should be(false)
    b.isRightLive(4) should be(true)
  }

  test("fromEdges, left") {
    val b = Boundaries.fromEdges(
      universe(),
      Seq(
        Side.right(BitSet(0, 2, 4))
      )
    )
    b.isLeftLive(0) should be(true)
    b.isLeftLive(1) should be(false)
    b.isLeftLive(2) should be(true)
    b.isLeftLive(3) should be(false)
    b.isLeftLive(4) should be(true)
  }
}
