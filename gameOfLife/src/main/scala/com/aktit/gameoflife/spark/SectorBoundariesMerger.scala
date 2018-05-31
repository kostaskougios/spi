package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Boundaries, Edges, Sector, Universe}
import com.aktit.gameoflife.spark.Directories.{turnEdgesDir, turnSectorDir, turnUniverseDir}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Merges the edges of surrounding sectors to create the Boundaries for a sector.
  *
  * @author kostas.kougios
  *         28/05/18 - 23:30
  */
class SectorBoundariesMerger(gameName: String, turn: Int) extends Serializable
{
	def merge(sc: SparkContext, out: String): RDD[Sector] = {
		// just load the universe
		val universe = sc.objectFile[Universe](turnUniverseDir(out, gameName, 1)).collect().head
		merge(
			universe,
			sc.objectFile[Edges](turnEdgesDir(out, gameName, turn)),
			sc.objectFile[Sector](turnSectorDir(out, gameName, turn))
		)
	}

	def merge(universe: Universe, edgesRDD: RDD[Edges], sectorRDD: RDD[Sector]): RDD[Sector] = {

		val boundaries = edgesToBoundaries(universe, edgesRDD)

		sectorRDD.map(sector => ((sector.posX, sector.posY), sector))
			.join(boundaries)
			.map {
				case (_, (sector, newBoundaries)) =>
					sector.withBoundaries(newBoundaries)
			}

	}

	private def edgesToBoundaries(universe: Universe, edges: RDD[Edges]) = {
		edges
			.flatMap(_.sendToNeighbors)
			.groupByKey()
			.mapValues(edges => Boundaries.fromEdges(universe, edges.toSeq))
	}
}
