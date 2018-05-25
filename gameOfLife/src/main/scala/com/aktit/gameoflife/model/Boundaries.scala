package com.aktit.gameoflife.model

import scala.collection.immutable.BitSet

/**
  * T=Top
  * B=Bottom
  * L=Left
  * R=Right
  *
  * TTTTTT
  * LS   R
  * L    R
  * BBBBBB
  *
  * S has coordinates of (0,0).
  *
  * This means that Top + Bottom X cordinates begin from -1
  *
  * @author kostas.kougios
  *         25/05/18 - 21:49
  */
trait Boundaries
{
	def isLeftLive(y: Int): Boolean

	def isRightLive(y: Int): Boolean

	def isTop(x: Int): Boolean

	def isBottom(x: Int): Boolean
}

object Boundaries
{
	def apply(
		width: Int,
		height: Int,
		top: Array[Int],
		bottom: Array[Int],
		left: Array[Int],
		right: Array[Int]
	): Boundaries = {
		check(top, -1, width)
		check(bottom, -1, width)
		check(left, 0, height - 1)
		check(right, 0, height - 1)
		BitSetBoundaries(BitSet(top.map(_ + 1): _*), BitSet(bottom.map(_ + 1): _*), BitSet(left: _*), BitSet(right: _*))
	}

	private def check(a: Array[Int], min: Int, max: Int) = for (i <- a) if (i < min || i > max) throw new IllegalArgumentException(s"coordinate out of bounds: $i should be between $min and $max")

	private case class BitSetBoundaries(top: BitSet, bottom: BitSet, left: BitSet, right: BitSet) extends Boundaries
	{
		def isLeftLive(y: Int): Boolean = left(y)

		def isRightLive(y: Int): Boolean = right(y)

		def isTop(x: Int): Boolean = top(x + 1)

		def isBottom(x: Int): Boolean = bottom(x + 1)
	}

}
