package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging

/**
  * @author kostas.kougios
  *         27/05/18 - 20:39
  */
case class CreateCmd(gameName: String, sectorWidth: Int, sectorHeight: Int, numSectorsHorizontal: Int, numSectorsVertical: Int, howManyLiveCells: Int) extends Command with Logging
{
	def run(sc: SparkContext, out: String) = {
		val outDir = out + "/" + gameName + "/turn-1"
		val sectorCoords = for {
			x <- 0 to numSectorsHorizontal.toInt
			y <- 0 to numSectorsVertical.toInt
		} yield (x, y)
		sc.parallelize(sectorCoords).map {
			case (x, y) =>
				logInfo(s"Creating sector at ($x,$y) for game $gameName")
				val matrix = Matrix.newBuilder(sectorWidth, sectorHeight).addRandomLiveCells(howManyLiveCells).result()
				Sector(matrix, Boundaries.empty(sectorWidth, sectorHeight))
		}.saveAsObjectFile(outDir)
	}
}
