package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.Sector
import com.aktit.gameoflife.spark.Directories.turnSectorDir
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
		val art = sc.objectFile[Sector](turnSectorDir(out, gameName, turn))
			.collect()
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
