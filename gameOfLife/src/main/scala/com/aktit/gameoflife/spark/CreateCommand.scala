package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging

/**
  * Creates a game.
  *
  * @author kostas.kougios
  *         27/05/18 - 20:39
  */
class CreateCommand(gameName: String, sectorWidth: Int, sectorHeight: Int, numSectorsHorizontal: Int, numSectorsVertical: Int, howManyLiveCells: Int) extends Command with Logging
{
	def run(sc: SparkContext, out: String) =
		sc.parallelize(sectorCoordinates).map {
			case (x, y) =>
				createSector(x, y)
		}.saveAsObjectFile(turnDir(out, gameName, 1))

	private def sectorCoordinates = for {
		x <- 0 until numSectorsHorizontal.toInt
		y <- 0 until numSectorsVertical.toInt
	} yield (x, y)

	private def createSector(x: Int, y: Int) = {
		logInfo(s"Creating sector at ($x,$y) for game $gameName")
		val matrix = Matrix.newBuilder(sectorWidth, sectorHeight).addRandomLiveCells(howManyLiveCells).result()
		val s = Sector(x, y, matrix, Boundaries.empty(sectorWidth, sectorHeight))
		logInfo(s"Now saving sector ($x,$y)")
		s
	}
}
