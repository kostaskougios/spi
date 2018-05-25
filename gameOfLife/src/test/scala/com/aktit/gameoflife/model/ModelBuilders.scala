package com.aktit.gameoflife.model

/**
  * @author kostas.kougios
  *         25/05/18 - 22:46
  */
object ModelBuilders
{
	def boundaries(
		width: Int = 10,
		height: Int = 5,
		top: Array[Int] = Array(),
		bottom: Array[Int] = Array(),
		left: Array[Int] = Array(),
		right: Array[Int] = Array()
	) = Boundaries(width, height, top, bottom, left, right)
}
