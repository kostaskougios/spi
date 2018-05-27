package com.aktit.gameoflife.benchmark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import com.aktit.utils.TimeMeasure.dt

/**
  * This helps me measure and optimize the domain model
  *
  * Run 1 : 90000 ms
  * Run 2 : 18000 ms - removed Int boxing and converted functional for-comprehensions to non-functional (Sector.liveNeighbours)
  * Run 3 : 15000 ms - swapped for ( x then y ) to for (y then x), probably better cpu cache usage
  * Run 4 : 10000 ms - .par for Sector.evolve
  * Run 5 : 2500 ms  - used non-functional (internally mutable) code in Sector.evolve
  *
  * @author kostas.kougios
  *         26/05/18 - 23:44
  */
object SectorEvolutionBenchmark extends App
{
	val Width = 100000
	val Height = 100000

	val width64 = Width / 64
	val sector = {
		println(s"Creating a sector with ${(Width * Height) / 1000000} million cells")
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
