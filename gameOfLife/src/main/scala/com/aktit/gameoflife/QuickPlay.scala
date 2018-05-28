package com.aktit.gameoflife

import com.aktit.gameoflife.spark.{CreateCommand, PlayCommand, SaveSectorAsciiCommand}
import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Test runs during dev. Plays a quick game and exports the ascii of all sectors for each turn.
  *
  * @author kostas.kougios
  *         28/05/18 - 23:04
  */
object QuickPlay extends Logging
{
	def main(args: Array[String]): Unit = {
		val Width = 60
		val Height = 20
		val StartWithHowManyLive = Width * Height / 5
		val GameName = "QuickPlay"

		val conf = new SparkConf().setAppName(getClass.getName)
		val out = "/tmp"

		val commands = Seq(
			new CreateCommand(GameName, Width, Height, 4, 4, StartWithHowManyLive)
		) ++ (1 to 10).flatMap {
			turn =>
				Seq(
					new PlayCommand(GameName, turn),
					new SaveSectorAsciiCommand(GameName, turn)
				)
		}

		val sc = new SparkContext(conf)
		try {
			commands.foreach(_.run(sc, out))
		} finally sc.stop()
	}
}
