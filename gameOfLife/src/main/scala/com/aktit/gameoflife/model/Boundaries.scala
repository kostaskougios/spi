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
	def width: Int

	def height: Int

	def isLeftLive(y: Int): Boolean

	def isRightLive(y: Int): Boolean

	def isTop(x: Int): Boolean

	def isBottom(x: Int): Boolean
}

object Boundaries
{
	// Compose a Boundaries from the 8 surrounding edges
	def fromEdges(edges: Seq[Edge]): Boundaries = {
		val bottomRightBoundaryCorner = edges.collectFirst {
			case e: TopLeftCorner => e.alive
		}.getOrElse(false)
		val bottomLeftBoundaryCorner = edges.collectFirst {
			case e: TopRightCorner => e.alive
		}.getOrElse(false)
		val bottomBoundary = edges.collectFirst {
			case e: TopSide => e
		}.getOrElse(Side.EmptyTop)
		???
	}

	def empty(
		width: Int,
		height: Int
	): Boundaries = apply(
		width,
		height,
		Array.empty[Int],
		Array.empty[Int],
		Array.empty[Int],
		Array.empty[Int]
	)

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
		BitSetBoundaries(width, height, BitSet(top.map(_ + 1): _*), BitSet(bottom.map(_ + 1): _*), BitSet(left: _*), BitSet(right: _*))
	}

	private def check(a: Array[Int], min: Int, max: Int) = for (i <- a) if (i < min || i > max) throw new IllegalArgumentException(s"coordinate out of bounds: $i should be between $min and $max")

	private case class BitSetBoundaries(width: Int, height: Int, top: BitSet, bottom: BitSet, left: BitSet, right: BitSet) extends Boundaries
	{
		// avoid BitSet.apply due to Int boxing

		def isLeftLive(y: Int): Boolean = left.contains(y)

		def isRightLive(y: Int): Boolean = right.contains(y)

		def isTop(x: Int): Boolean = top.contains(x + 1)

		def isBottom(x: Int): Boolean = bottom.contains(x + 1)
	}

}
