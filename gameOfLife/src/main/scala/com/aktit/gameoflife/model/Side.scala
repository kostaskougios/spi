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
trait Side
{

}

trait TopSide extends Side

trait BottomSide extends Side

trait LeftSide extends Side

trait RightSide extends Side

object Side
{
	def top(s: BitSet): TopSide = BitSetTopSide(s)

	def bottom(s: BitSet): BottomSide = BitSetBottomSide(s)

	def left(s: BitSet): LeftSide = BitSetLeftSide(s)

	def right(s: BitSet): RightSide = BitSetRightSide(s)

	private case class BitSetTopSide(top: BitSet) extends TopSide

	private case class BitSetBottomSide(top: BitSet) extends BottomSide

	private case class BitSetLeftSide(top: BitSet) extends LeftSide

	private case class BitSetRightSide(top: BitSet) extends RightSide

}