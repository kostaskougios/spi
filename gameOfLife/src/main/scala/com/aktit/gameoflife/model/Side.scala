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
	// Sector's posX that this side belongs to
	def posX: Int

	// Sector's posY that this side belongs to
	def posY: Int
}

trait TopSide extends Side

trait BottomSide extends Side

trait LeftSide extends Side

trait RightSide extends Side

object Side
{
	def top(posX: Int, posY: Int, s: BitSet): TopSide = BitSetTopSide(posX, posY, s)

	def bottom(posX: Int, posY: Int, s: BitSet): BottomSide = BitSetBottomSide(posX, posY, s)

	def left(posX: Int, posY: Int, s: BitSet): LeftSide = BitSetLeftSide(posX, posY, s)

	def right(posX: Int, posY: Int, s: BitSet): RightSide = BitSetRightSide(posX, posY, s)

	private case class BitSetTopSide(posX: Int, posY: Int, top: BitSet) extends TopSide

	private case class BitSetBottomSide(posX: Int, posY: Int, top: BitSet) extends BottomSide

	private case class BitSetLeftSide(posX: Int, posY: Int, top: BitSet) extends LeftSide

	private case class BitSetRightSide(posX: Int, posY: Int, top: BitSet) extends RightSide

}