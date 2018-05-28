package com.aktit.gameoflife.model

/**
  * Each Sector extracts it's corner cells to be used by neighbouring sectors when Sector.evolve is called.
  * This is because on the edges of the sector, cells require neighbouring cells diagonally in order to evolve.
  * Because with spark we calculate each sector separately, we use this to know the live cells near
  * the edges.
  *
  * @author kostas.kougios
  *         28/05/18 - 01:05
  */
trait Corner
{
	def alive: Boolean
}

trait TopLeftCorner extends Corner

trait TopRightCorner extends Corner

trait BottomLeftCorner extends Corner

trait BottomRightCorner extends Corner

object Corner
{
	def topLeft(alive: Boolean): TopLeftCorner = BooleanTopLeftCorner(alive)

	def topRight(alive: Boolean): TopRightCorner = BooleanTopRightCorner(alive)

	def bottomLeft(alive: Boolean): BottomLeftCorner = BooleanBottomLeftCorner(alive)

	def bottomRight(alive: Boolean): BottomRightCorner = BooleanBottomRightCorner(alive)

	case class BooleanTopLeftCorner(alive: Boolean) extends TopLeftCorner

	case class BooleanTopRightCorner(alive: Boolean) extends TopRightCorner

	case class BooleanBottomLeftCorner(alive: Boolean) extends BottomLeftCorner

	case class BooleanBottomRightCorner(alive: Boolean) extends BottomRightCorner

}