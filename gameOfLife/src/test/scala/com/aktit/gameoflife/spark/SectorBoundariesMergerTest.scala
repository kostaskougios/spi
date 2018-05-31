package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.ModelBuilders.{edges, sector, universe}
import com.aktit.spark.testing.BaseSparkSuite

/**
  * @author kostas.kougios
  *         29/05/18 - 11:12
  */
class SectorBoundariesMergerTest extends BaseSparkSuite
{
	val merger = new SectorBoundariesMerger("test", 1)

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
