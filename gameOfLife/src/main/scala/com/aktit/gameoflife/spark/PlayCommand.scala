package com.aktit.gameoflife.spark

import com.aktit.gameoflife.spark.Directories._
import com.aktit.utils.TimeMeasure
import org.apache.spark.SparkContext
import org.apache.spark.api.java.StorageLevels
import org.apache.spark.internal.Logging

/**
  * @author kostas.kougios
  *         27/05/18 - 21:27
  */
class PlayCommand(gameName: String, turn: Int) extends Command with Logging
{
	override def run(sc: SparkContext, out: String): Unit = {
		logInfo(s"Will now play $gameName turn $turn")

		val rdd = new SectorBoundariesMerger(gameName, turn)
			.merge(sc, out)
			.map {
				sector =>
					logInfo(s"Evolving sector (${sector.posX},${sector.posY})")
					val (t, s) = TimeMeasure.dt(sector.evolve)
					logInfo(s"Evolving sector (${sector.posX},${sector.posY}) took $t ms")
					s

			}.persist(StorageLevels.DISK_ONLY)
		rdd.saveAsObjectFile(turnSectorDir(out, gameName, turn + 1))
		rdd.map(_.edges).saveAsObjectFile(turnEdgesDir(out, gameName, turn + 1))
	}
}
