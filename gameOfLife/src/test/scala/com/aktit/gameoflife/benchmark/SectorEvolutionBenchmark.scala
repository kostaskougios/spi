package com.aktit.gameoflife.benchmark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import com.aktit.utils.TimeMeasure.dt

/**
  * This helps me measure and optimize the domain model
  *
  * Run 1 : 2000 ms
  *
  * @author kostas.kougios
  *         26/05/18 - 23:44
  */
object SectorEvolutionBenchmark extends App
{
	val Width = 10000
	val Height = 10000

	val width64 = Width / 64
	val sector = {
		println(s"Creating ${(Width * Height) / 1000000} million live nodes")
		val matrix = Matrix.fastRandom(width64, Height)
		Sector(matrix, Boundaries.empty(width64 * 64, Height))
	}

	println("Evolution begins!")

	for (i <- 1 to 10000) {
		val (time, _) = dt {
			sector.evolve
		}
		println(s"$i : $time millis")
	}
}
