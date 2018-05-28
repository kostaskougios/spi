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