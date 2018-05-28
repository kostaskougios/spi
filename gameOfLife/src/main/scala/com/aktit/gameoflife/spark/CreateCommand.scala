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
class CreateCommand(gameName: String, sectorWidth: Int, sectorHeight: Int, numSectorsHorizontal: Int, numSectorsVertical: Int, howManyLiveCells: Int)
	extends GameCommand with Logging
{
	def run(sc: SparkContext, out: String) = {
		val rdd = sc.parallelize(sectorCoordinates).map {
			case (x, y) =>
				createSector(x, y)
		}
		val (edges, sectors) = includeEdges(rdd)
		edges.saveAsObjectFile(turnEdgesDir(out, gameName, 1))
		val x = sectors.map(partitionSector)
			.groupByKey() // Partitions by (x,y) so that sectors and edges are in the same partition
			.mapValues(_.head) // there is only one
			.saveAsObjectFile(turnDir(out, gameName, 1))
	}

	private def sectorCoordinates = for {
		x <- 0 until numSectorsHorizontal
		y <- 0 until numSectorsVertical
	} yield (x, y)

	private def createSector(x: Int, y: Int) = {
		logInfo(s"Creating sector at ($x,$y) for game $gameName")
		val matrix = Matrix.newBuilder(sectorWidth, sectorHeight).addRandomLiveCells(howManyLiveCells).result()
		val s = Sector(x, y, matrix, Boundaries.empty(sectorWidth, sectorHeight))
		logInfo(s"Now saving sector ($x,$y)")
		s
	}
}
