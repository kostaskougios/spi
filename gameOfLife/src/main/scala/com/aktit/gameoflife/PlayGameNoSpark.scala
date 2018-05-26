package com.aktit.gameoflife

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}

/**
  * @author kostas.kougios
  *         26/05/18 - 23:20
  */
object PlayGameNoSpark extends App
{
	val Width = 60
	val Height = 20
	val StartWithHowManyLive = Width * Height / 10

	val matrix = Matrix.random(Width, Height, StartWithHowManyLive)
	var sector = Sector(matrix, Boundaries.empty(Width, Height))
	while (true) {
		println(sector.toAscii)
		sector = sector.evolve
		Thread.sleep(1000)
	}
}
