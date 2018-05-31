package com.aktit.gameoflife.benchmark

import com.aktit.gameoflife.model.Matrix
import com.aktit.utils.TimeMeasure.dt

/**
  * This helps me measure and optimize the domain model
  *
  * Run 1: dt = 3000 ms
  * Run 2: dt = 2200 (Tuples.tupleField1ToIntArray)
  *
  * @author kostas.kougios
  *         26/05/18 - 23:44
  */
object MatrixCreationBenchmark extends App
{
	val Width = 10000
	val Height = 10000

	for (i <- 1 to 10000) {
		val (time, _) = dt {
			Matrix.newBuilder(Width, Height).addRandomLiveCells(20000000).result()
		}
		println(s"$i : $time")
	}
}
