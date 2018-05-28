package com.aktit.gameoflife.model

import scala.collection.immutable.BitSet

/**
  * Each Sector extracts it's side's to be used by neighbouring sectors when Sector.evolve is called.
  * This is because on the edges of the sector, cells require neighbouring cells in order to evolve.
  * Because with spark we calculate each sector separately, we use this to know the live cells near
  * the edges.
  *
  * @author kostas.kougios
  *         28/05/18 - 00:47
  */
trait Side extends Edge
{
	def isLive(pos: Int): Boolean

	def allLive: Seq[Int]
}

trait TopSide extends Side

trait BottomSide extends Side

trait LeftSide extends Side

trait RightSide extends Side

object Side
{
	val EmptyTop = top(BitSet.empty)

	def top(s: BitSet): TopSide = BitSetTopSide(s)

	def bottom(s: BitSet): BottomSide = BitSetBottomSide(s)

	def left(s: BitSet): LeftSide = BitSetLeftSide(s)

	def right(s: BitSet): RightSide = BitSetRightSide(s)

	private case class BitSetTopSide(top: BitSet) extends TopSide
	{
		override def isLive(pos: Int) = top.contains(pos)

		override def allLive: Seq[Int] = top.toSeq
	}

	private case class BitSetBottomSide(bottom: BitSet) extends BottomSide
	{
		override def isLive(pos: Int) = bottom.contains(pos)

		override def allLive: Seq[Int] = bottom.toSeq
	}

	private case class BitSetLeftSide(left: BitSet) extends LeftSide
	{
		override def isLive(pos: Int) = left.contains(pos)

		override def allLive: Seq[Int] = left.toSeq

	}

	private case class BitSetRightSide(right: BitSet) extends RightSide
	{
		override def isLive(pos: Int) = right.contains(pos)

		override def allLive: Seq[Int] = right.toSeq
	}

}