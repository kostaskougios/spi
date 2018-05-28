package com.aktit.gameoflife.spark

import org.apache.spark.SparkContext

/**
  * Note: not meant to scale. Just for small sectors, useful when running locally during dev.
  *
  * @author kostas.kougios
  *         28/05/18 - 23:13
  */
class SaveSectorAsciiCommand(gameName: String, turn: Int) extends Command
{
	override def run(sc: SparkContext, out: String): Unit = {
		val rdd = new SectorBoundariesMerger(gameName, turn)
			.merge(sc, out)

		val art = rdd.collect()
			.groupBy(_.posY)
			.toSeq
			.sortBy(_._1)
			.map {
				case (y, sectors) =>
					sectors.toSeq
						.sortBy(_.posX)
						.map(_.toAscii.split("\n").toSeq)
						.transpose
						.map(_.mkString(" | "))
						.mkString("\n")
			}.mkString("\n-----------------------------------------------------\n")
		println(s"---------- TURN $turn -------------------")
		println(art)
	}
}
