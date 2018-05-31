package com.aktit.gameoflife.model

/**
  * @author kostas.kougios
  *         28/05/18 - 01:21
  */
case class Edges(
	// Sector's posX that these edges belongs to
	posX: Int,

	// Sector's posY that these edges belongs to
	posY: Int,
	topLeftCorner: TopLeftCorner,
	topRightCorner: TopRightCorner,
	bottomLeftCorner: BottomLeftCorner,
	bottomRightCorner: BottomRightCorner,
	topSide: TopSide,
	bottomSide: BottomSide,
	leftSide: LeftSide,
	rightSide: RightSide
)
{
	def sendToNeighbors = {
		// this is bit confusing but we need to send the correct edge to the correct (x,y) coordinates of the sector
		// that requires it for it's Boundaries.
		Seq(
			((posX + 1, posY - 1), topRightCorner),
			((posX + 1, posY + 1), bottomRightCorner),
			((posX - 1, posY - 1), topLeftCorner),
			((posX - 1, posY + 1), bottomLeftCorner),
			((posX + 1, posY), rightSide),
			((posX - 1, posY), leftSide),
			((posX, posY - 1), topSide),
			((posX, posY + 1), bottomSide)
		)
	}
}