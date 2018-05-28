package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Edges, Sector}
import org.apache.spark.rdd.RDD

/**
  * @author kostas.kougios
  *         28/05/18 - 20:35
  */
trait GameCommand extends Command
{
	protected def turnDir(out: String, gameName: String, turn: Int) = out + "/" + gameName + s"/turn-" + turn

	protected def turnEdgesDir(out: String, gameName: String, turn: Int) = out + "/" + gameName + s"/turn-" + turn + "-edges"

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
}
