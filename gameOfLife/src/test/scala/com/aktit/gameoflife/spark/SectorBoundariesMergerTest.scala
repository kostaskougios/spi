package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.ModelBuilders.edges
import com.aktit.gameoflife.model._
import com.aktit.spark.testing.BaseSparkSuite

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

}
