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

	def isTopLive(x: Int): Boolean

	def isBottomLive(x: Int): Boolean
}

object Boundaries
{
	// Compose a Boundaries from the 8 surrounding edges
	def fromEdges(universe: Universe, edges: Seq[Edge]): Boundaries = {
		val bottomRightBoundaryCorner = edges.collectFirst {
			case e: TopLeftCorner if e.alive => universe.sectorWidth
		}.toArray
		val bottomLeftBoundaryCorner = edges.collectFirst {
			case e: TopRightCorner if e.alive => -1
		}.toArray
		val bottomBoundary = edges.collectFirst {
			case e: TopSide => e.allLive
		}.getOrElse(Seq.empty)

		val topRightBoundaryCorner = edges.collectFirst {
			case e: BottomLeftCorner if e.alive => universe.sectorWidth
		}.toArray
		val topLeftBoundaryCorner = edges.collectFirst {
			case e: BottomRightCorner if e.alive => -1
		}.toArray
		val topBoundary = edges.collectFirst {
			case e: BottomSide => e.allLive
		}.getOrElse(Seq.empty)

		val left = edges.collectFirst {
			case e: RightSide => e.allLive
		}.getOrElse(Seq.empty)

		val right = edges.collectFirst {
			case e: LeftSide => e.allLive
		}.getOrElse(Seq.empty)

		apply(
			universe.sectorWidth,
			universe.sectorHeight,
			topRightBoundaryCorner ++ topBoundary ++ topLeftBoundaryCorner,
			bottomRightBoundaryCorner ++ bottomBoundary ++ bottomLeftBoundaryCorner,
			left.toArray,
			right.toArray
		)
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

		def isTopLive(x: Int): Boolean = top.contains(x + 1)

		def isBottomLive(x: Int): Boolean = bottom.contains(x + 1)
	}

}
