package com.aktit.gameoflife.model

/**
  * @author kostas.kougios
  *         28/05/18 - 01:21
  */
case class Edges(
	topLeftCorner: TopLeftCorner,
	topRightCorner: TopRightCorner,
	bottomLeftCorner: BottomLeftCorner,
	bottomRightCorner: BottomRightCorner,
	topSide: TopSide,
	bottomSide: BottomSide,
	leftSide: LeftSide,
	rightSide: RightSide
)