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

	private case class BitSetBoundaries(top: BitSet, bottom: BitSet, left: BitSet, right: BitSet) extends Boundaries
	{
		def isLeftLive(y: Int): Boolean = left(y)

		def isRightLive(y: Int): Boolean = right(y)

		def isTop(x: Int): Boolean = top(x + 1)

		def isBottom(x: Int): Boolean = bottom(x + 1)
	}

}
