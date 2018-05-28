package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Boundaries, Edges, Sector}
import com.aktit.gameoflife.spark.Directories._
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
/**
  * @author kostas.kougios
  *         27/05/18 - 21:27
  */
class PlayCommand(gameName: String, turn: Int) extends GameCommand with Logging
{
	override def run(sc: SparkContext, out: String): Unit = {
		logInfo(s"Will now play $gameName turn $turn")

		val rdd = sc.objectFile[Sector](turnSectorDir(out, gameName, turn)).map {
			sector =>
				logInfo(s"Evolving sector (${sector.posX},${sector.posY})")
				sector.evolve
		}
		val (edges, sectors) = includeEdges(rdd)
		edges.saveAsObjectFile(turnEdgesDir(out, gameName, turn + 1))
		sectors.saveAsObjectFile(turnSectorDir(out, gameName, turn + 1))
	}

	private def edgesToBoundaries(sc: SparkContext, out: String) = {
		sc.objectFile[Edges](turnEdgesDir(out, gameName, turn))
			.flatMap(partitionEdges)
			.groupByKey()
			.mapValues(edges => Boundaries.fromEdges(edges.toSeq))
	}
}
