package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.edges
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

import scala.collection.immutable.BitSet

/** @author
  *   kostas.kougios 31/05/18 - 18:06
  */
class EdgesTest extends AnyFunSuite {
  test("partitions edges, top left corner") {
    edges(topLeftCorner = Corner.topLeft(true)).sendToNeighbors.collect { case ((0, 0), c: TopLeftCorner) =>
      c
    } should be(Seq(Corner.topLeft(true)))
  }

  test("partitions edges, top right corner") {
    edges(topRightCorner = Corner.topRight(true)).sendToNeighbors.collect { case ((2, 0), c: TopRightCorner) =>
      c
    } should be(Seq(Corner.topRight(true)))
  }

  test("partitions edges, bottom left corner") {
    edges(bottomLeftCorner = Corner.bottomLeft(true)).sendToNeighbors.collect { case ((0, 2), c: BottomLeftCorner) =>
      c
    } should be(Seq(Corner.bottomLeft(true)))
  }

  test("partitions edges, bottom right corner") {
    edges(bottomRightCorner = Corner.bottomRight(true)).sendToNeighbors.collect { case ((2, 2), c: BottomRightCorner) =>
      c
    } should be(Seq(Corner.bottomRight(true)))
  }

  test("partitions edges, top side") {
    val side = Side.top(BitSet(5, 6))
    edges(topSide = side).sendToNeighbors.collect { case ((1, 0), c: TopSide) =>
      c
    } should be(Seq(side))
  }

  test("partitions edges, bottom side") {
    val side = Side.bottom(BitSet(5, 6))
    edges(bottomSide = side).sendToNeighbors.collect { case ((1, 2), c: BottomSide) =>
      c
    } should be(Seq(side))
  }

  test("partitions edges, left side") {
    val side = Side.left(BitSet(5, 6))
    edges(leftSide = side).sendToNeighbors.collect { case ((0, 1), c: LeftSide) =>
      c
    } should be(Seq(side))
  }

  test("partitions edges, right side") {
    val side = Side.right(BitSet(5, 6))
    edges(rightSide = side).sendToNeighbors.collect { case ((2, 1), c: RightSide) =>
      c
    } should be(Seq(side))
  }

}
