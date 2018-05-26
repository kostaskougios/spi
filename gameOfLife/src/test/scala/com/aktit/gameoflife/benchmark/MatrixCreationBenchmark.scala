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
	val Width = 4000
	val Height = 4000

	println(s"Creating ${Width * Height} live nodes")
	val live = for {
		x <- 0 until Width
		y <- 0 until Height
	} yield (x, y)

	for (i <- 1 to 10000) {
		val (time, _) = dt {
			Matrix(Width, Height, live)
		}
		println(s"$i : $time")
	}
}
