package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Boundaries, Edges, Sector, Universe}
import com.aktit.gameoflife.spark.Directories._
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD

/**
  * @author kostas.kougios
  *         27/05/18 - 21:27
  */
class PlayCommand(gameName: String, turn: Int) extends GameCommand with Logging
{
	override def run(sc: SparkContext, out: String): Unit = {
		logInfo(s"Will now play $gameName turn $turn")
		// just load the universe
		val universe = sc.objectFile[Universe](turnUniverseDir(out, gameName, turn)).collect().head

		val boundaries = edgesToBoundaries(universe, sc.objectFile[Edges](turnEdgesDir(out, gameName, turn)))

		val rdd = sc.objectFile[Sector](turnSectorDir(out, gameName, turn))
			.map(sector => ((sector.posX, sector.posY), sector))
			.join(boundaries)
			.map {
				case (_, (sector, newBoundaries)) =>
					logInfo(s"Evolving sector (${sector.posX},${sector.posY})")
					sector.withBoundaries(newBoundaries).evolve
			}
		val (edges, sectors) = includeEdges(rdd)
		edges.saveAsObjectFile(turnEdgesDir(out, gameName, turn + 1))
		sectors.saveAsObjectFile(turnSectorDir(out, gameName, turn + 1))
	}

	private def edgesToBoundaries(universe: Universe, edges: RDD[Edges]) = {
		edges
			.flatMap(partitionEdges)
			.groupByKey()
			.mapValues(edges => Boundaries.fromEdges(universe, edges.toSeq))
	}
}
