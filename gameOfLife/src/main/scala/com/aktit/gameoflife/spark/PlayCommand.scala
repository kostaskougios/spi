package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Edges, Sector}
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

		val rdd = sc.objectFile[Sector](turnDir(out, gameName, turn)).map {
			sector =>
				logInfo(s"Evolving sector (${sector.posX},${sector.posY})")
				sector.evolve
		}
		val (edges, sectors) = includeEdges(rdd)
		edges.saveAsObjectFile(turnEdgesDir(out, gameName, turn + 1))
		sectors.saveAsObjectFile(turnDir(out, gameName, turn + 1))
	}

	private def edges(sc: SparkContext, out: String) = {
		sc.objectFile[Edges](turnEdgesDir(out, gameName, turn)).map {
			edges =>
				// Edges are going to be needed for boundaries of other sectors.
				// We need to send each edge to the correct partition.
				Seq(
					edges.topLeftCorner
				)
		}
	}
}
