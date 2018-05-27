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
  * Run 6 : 3500 ms  - standarise Matrix creation and use Matrix.newBuilder
  *
  * @author kostas.kougios
  *         26/05/18 - 23:44
  */
object SectorEvolutionBenchmark extends App
{
	val Width = 10000
	val Height = 10000

	val sector = {
		println(s"Creating a sector with ${(Width * Height) / 1000000} million cells")
		val matrix = Matrix.newBuilder(Width, Height).addRandom(Width * Height / 10).result()
		Sector(matrix, Boundaries.empty(Width, Height))
	}

	println("Evolution begins!")

	for (i <- 1 to 10000) {
		val (time, _) = dt {
			sector.evolve
		}
		println(s"$i : $time millis")
	}
}
