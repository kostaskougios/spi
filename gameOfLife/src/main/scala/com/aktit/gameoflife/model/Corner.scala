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
	// Sector's posX that this side belongs to
	def posX: Int

	// Sector's posY that this side belongs to
	def posY: Int

	def alive: Boolean
}

trait TopLeftCorner extends Corner

trait TopRightCorner extends Corner

trait BottomLeftCorner extends Corner

trait BottomRightCorner extends Corner

object Corner
{
	def topLeft(posX: Int, posY: Int, alive: Boolean): TopLeftCorner = BooleanTopLeftCorner(posX, posY, alive)

	def topRight(posX: Int, posY: Int, alive: Boolean): TopRightCorner = BooleanTopRightCorner(posX, posY, alive)

	def bottomLeft(posX: Int, posY: Int, alive: Boolean): BottomLeftCorner = BooleanBottomLeftCorner(posX, posY, alive)

	def bottomRight(posX: Int, posY: Int, alive: Boolean): BottomRightCorner = BooleanBottomRightCorner(posX, posY, alive)

	case class BooleanTopLeftCorner(posX: Int, posY: Int, alive: Boolean) extends TopLeftCorner

	case class BooleanTopRightCorner(posX: Int, posY: Int, alive: Boolean) extends TopRightCorner

	case class BooleanBottomLeftCorner(posX: Int, posY: Int, alive: Boolean) extends BottomLeftCorner

	case class BooleanBottomRightCorner(posX: Int, posY: Int, alive: Boolean) extends BottomRightCorner

}