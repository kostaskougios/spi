package com.aktit.gameoflife.spark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector, Universe}
import com.aktit.gameoflife.spark.Directories._
import com.aktit.utils.TimeMeasure
import org.apache.spark.SparkContext
import org.apache.spark.api.java.StorageLevels
import org.apache.spark.internal.Logging

/**
  * Creates a game.
  *
  * @author kostas.kougios
  *         27/05/18 - 20:39
  */
class CreateCommand(gameName: String, sectorWidth: Int, sectorHeight: Int, numSectorsHorizontal: Int, numSectorsVertical: Int, howManyLiveCellsPerSector: Int)
	extends Command with Logging
{
	private val universe = Universe(gameName, numSectorsHorizontal, numSectorsVertical, sectorWidth, sectorHeight)
	def run(sc: SparkContext, out: String) = {
		val rdd = sc.parallelize(sectorCoordinates, numSectorsHorizontal * numSectorsVertical /* save more files to avoid Hadoop array size exceeding 2GB */).map {
			case (x, y) =>
				createSector(x, y)
		}.persist(StorageLevels.DISK_ONLY) // These will use too much memory and will spill to disk anyway
		sc.parallelize(Seq(universe)).saveAsObjectFile(turnUniverseDir(out, gameName, 1))
		rdd.saveAsObjectFile(turnSectorDir(out, gameName, 1))
		rdd.map(_.edges).saveAsObjectFile(turnEdgesDir(out, gameName, 1))
	}

	private def sectorCoordinates = for {
		x <- 0 until numSectorsHorizontal
		y <- 0 until numSectorsVertical
	} yield (x, y)

	private def createSector(x: Int, y: Int) = {
		logInfo(s"Creating sector at ($x,$y) for game $gameName")

		val (t, s) = TimeMeasure.dt {
			val matrix = Matrix.newBuilder(sectorWidth, sectorHeight).addRandomLiveCells(howManyLiveCellsPerSector).result()
			Sector(x, y, matrix, Boundaries.empty(sectorWidth, sectorHeight))
		}
		logInfo(s"Done creating sector ($x,$y) in $t ms")
		s
	}
}
