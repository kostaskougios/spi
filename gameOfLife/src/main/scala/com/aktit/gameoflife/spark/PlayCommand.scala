package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.Sector
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging

/**
  * @author kostas.kougios
  *         27/05/18 - 21:27
  */
case class PlayCommand(gameName: String, turn: Int) extends Command with Logging
{
	override def run(sc: SparkContext, out: String): Unit = {
		logInfo(s"Will now play $gameName turn $turn")
		sc.objectFile[Sector](turnDir(out, gameName, turn)).map {
			sector =>
				logInfo("Evolving sector")
				sector.evolve
		}.saveAsObjectFile(turnDir(out, gameName, turn + 1))
	}
}
