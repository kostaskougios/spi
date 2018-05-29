package com.aktit.gameoflife

import com.aktit.gameoflife.spark.{CreateCommand, PlayCommand}
import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Create and play a number of turns of the game
  *
  * @author kostas.kougios
  *         28/05/18 - 23:04
  */
object CreateAndPlayOnSpark extends Logging
{
	def main(args: Array[String]): Unit = {

		val conf = new SparkConf()
			.setAppName(getClass.getName)
		val out = conf.get("spark.out")
		val width = conf.get("spark.width").toInt
		val height = conf.get("spark.height").toInt
		val numOfSectorsHorizontally = conf.get("spark.num-of-sectors-horizontally").toInt
		val numOfSectorsVertically = conf.get("spark.num-of-sectors-vertically").toInt
		val numLive = conf.get("spark.num-live").toInt
		val name = conf.get("spark.game-name")
		val turns = conf.get("spark.turns").toInt

		val commands = Seq(
			new CreateCommand(name, width, height, numOfSectorsHorizontally, numOfSectorsVertically, numLive)
		) ++ (1 to turns).map {
			turn =>
				new PlayCommand(name, turn)
		}

		val sc = new SparkContext(conf)
		try {
			commands.foreach(_.run(sc, out))
		} finally sc.stop()
	}
}
