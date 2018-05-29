package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.ModelBuilders.{edges, sector, universe}
import com.aktit.gameoflife.model._
import com.aktit.spark.testing.BaseSparkSuite

import scala.collection.immutable.BitSet

/**
  * @author kostas.kougios
  *         29/05/18 - 11:12
  */
class SectorBoundariesMergerTest extends BaseSparkSuite
{
	val merger = new SectorBoundariesMerger("test", 1)

	test("partitions edges, top left corner") {
		merger.partitionEdges(edges(topLeftCorner = Corner.topLeft(true))).collect {
			case ((0, 0), c: TopLeftCorner) => c
		} should be(Seq(Corner.topLeft(true)))
	}

	test("partitions edges, top right corner") {
		merger.partitionEdges(edges(topRightCorner = Corner.topRight(true))).collect {
			case ((2, 0), c: TopRightCorner) => c
		} should be(Seq(Corner.topRight(true)))
	}

	test("partitions edges, bottom left corner") {
		merger.partitionEdges(edges(bottomLeftCorner = Corner.bottomLeft(true))).collect {
			case ((0, 2), c: BottomLeftCorner) => c
		} should be(Seq(Corner.bottomLeft(true)))
	}

	test("partitions edges, bottom right corner") {
		merger.partitionEdges(edges(bottomRightCorner = Corner.bottomRight(true))).collect {
			case ((2, 2), c: BottomRightCorner) => c
		} should be(Seq(Corner.bottomRight(true)))
	}

	test("partitions edges, top side") {
		val side = Side.top(BitSet(5, 6))
		merger.partitionEdges(edges(topSide = side)).collect {
			case ((1, 0), c: TopSide) => c
		} should be(Seq(side))
	}

	test("partitions edges, bottom side") {
		val side = Side.bottom(BitSet(5, 6))
		merger.partitionEdges(edges(bottomSide = side)).collect {
			case ((1, 2), c: BottomSide) => c
		} should be(Seq(side))
	}

	test("partitions edges, left side") {
		val side = Side.left(BitSet(5, 6))
		merger.partitionEdges(edges(leftSide = side)).collect {
			case ((0, 1), c: LeftSide) => c
		} should be(Seq(side))
	}

	test("partitions edges, right side") {
		val side = Side.right(BitSet(5, 6))
		merger.partitionEdges(edges(rightSide = side)).collect {
			case ((2, 1), c: RightSide) => c
		} should be(Seq(side))
	}

	test("merge") {
		val edgesRDD = sc.parallelize(Seq(
			edges(0, 0),
			edges(1, 0),
			edges(2, 0),
			edges(0, 1),
			edges(0, 2),
			edges(2, 0),
			edges(2, 1),
			edges(2, 2),
			edges(1, 2)
		))
		val sectorsRDD = sc.parallelize(Seq(
			sector(posX = 1, posY = 1)
		))
		val mergedBoundaries = merger.merge(universe(), edgesRDD, sectorsRDD).collect().head.boundaries
		mergedBoundaries.isTopLive(-1) should be(true)
		mergedBoundaries.isTopLive(2) should be(true)
		mergedBoundaries.isTopLive(6) should be(true)
		mergedBoundaries.isTopLive(8) should be(true)
		mergedBoundaries.isTopLive(10) should be(true)

		mergedBoundaries.isLeftLive(2) should be(true)
		mergedBoundaries.isLeftLive(4) should be(true)

		mergedBoundaries.isRightLive(1) should be(true)
		mergedBoundaries.isRightLive(2) should be(true)
		mergedBoundaries.isRightLive(3) should be(true)

		mergedBoundaries.isBottomLive(-1) should be(true)
		mergedBoundaries.isBottomLive(10) should be(true)
		mergedBoundaries.isBottomLive(1) should be(true)
		mergedBoundaries.isBottomLive(5) should be(true)
		mergedBoundaries.isBottomLive(9) should be(true)

	}
}
