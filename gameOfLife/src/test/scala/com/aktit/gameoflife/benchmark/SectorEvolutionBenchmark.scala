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
	val Width = 2000
	val Height = 2000

	println(s"Creating ${Width * Height} random matrix")
	val matrix = Matrix.random(Width, Height, Width * Height / 10)
	val sector = Sector(matrix, Boundaries.empty(Width, Height))

	for (i <- 1 to 10000) {
		val (time, _) = dt {
			sector.evolve
		}
		println(s"$i : $time")
	}
}
