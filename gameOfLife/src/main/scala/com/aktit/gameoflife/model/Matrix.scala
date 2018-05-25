package com.aktit.gameoflife.model

import scala.collection.immutable.BitSet

/**
  * @author kostas.kougios
  *         25/05/18 - 22:20
  */
trait Matrix
{
	def width: Int

	def height: Int

	def isLive(x: Int, y: Int): Boolean
}

object Matrix
{

	private case class BitSetMatrix(width: Int, height: Int, data: Array[BitSet]) extends Matrix
	{
		if (data.length != height) throw new IllegalArgumentException(s"expected array length to match the height of the matrix : ${data.length}!=$height")

		override def isLive(x: Int, y: Int): Boolean = {
			if (x < 0 || x > width) throw new IllegalArgumentException(s"x is out of bounds : $x")
			if (y < 0 || y > height) throw new IllegalArgumentException(s"y is out of bounds : $y")
			data(y)(x)
		}
	}

}