package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Edges, Sector}
import org.apache.spark.rdd.RDD

/**
  * @author kostas.kougios
  *         28/05/18 - 20:35
  */
trait GameCommand extends Command
{
	protected def includeEdges(rdd: RDD[Sector]): (RDD[Edges], RDD[Sector]) = {
		val cached = rdd.cache()
		(cached.map(_.edges), cached)
	}

	// Sectors and edges are partitioned by their coordinates to limit shuffle
	def sectorPartitionKey(posX: Int, posY: Int): (Int, Int) = (posX, posY)

	/**
	  * Partitions the sectors so that sectors and edges are on the same partition (to reduce shuffle)
	  */
	def partitionSector(sector: Sector): ((Int, Int), Sector) = (sectorPartitionKey(sector.posX, sector.posY), sector)

	def partitionEdges(edges: Edges) = {
		// this is bit confusing but we need to send the correct edge to the correct (x,y) coordinates of the sector
		// that requires it for it's Boundaries.
		Seq(
			((edges.posX + 1, edges.posY - 1), edges.topRightCorner),
			((edges.posX + 1, edges.posY + 1), edges.bottomRightCorner),
			((edges.posX - 1, edges.posY - 1), edges.topLeftCorner),
			((edges.posX - 1, edges.posY + 1), edges.bottomLeftCorner),
			((edges.posX + 1, edges.posY), edges.rightSide),
			((edges.posX - 1, edges.posY), edges.leftSide),
			((edges.posX, edges.posY - 1), edges.topSide),
			((edges.posX, edges.posY + 1), edges.bottomSide)
		)
	}
}
