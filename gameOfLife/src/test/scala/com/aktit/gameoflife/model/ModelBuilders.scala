package com.aktit.gameoflife.model

import scala.collection.immutable.BitSet

/**
  * @author kostas.kougios
  *         25/05/18 - 22:46
  */
object ModelBuilders
{
	def universe(
		name: String = "universe-name",
		width: Int = 4,
		height: Int = 8,
		sectorWidth: Int = 10,
		sectorHeight: Int = 5
	) = Universe(
		name,
		width,
		height,
		sectorWidth,
		sectorHeight
	)
	def boundaries(
		width: Int = 10,
		height: Int = 5,
		top: Array[Int] = Array(),
		bottom: Array[Int] = Array(),
		left: Array[Int] = Array(),
		right: Array[Int] = Array()
	) = Boundaries(width, height, top, bottom, left, right)

	def matrix(width: Int = 10, height: Int = 5, liveCoordinates: Seq[(Int, Int)] = Nil): Matrix =
		Matrix(
			width,
			height,
			liveCoordinates
		)

	def sector(
		posX: Int = 0,
		posY: Int = 0,
		width: Int = 10,
		height: Int = 5,
		liveCoordinates: Seq[(Int, Int)] = Nil,
		boundaries: Boundaries = ModelBuilders.boundaries()
	) = Sector(posX, posY, width, height, liveCoordinates, boundaries)

	def edges(
		// Sector's posX that these edges belongs to
		posX: Int = 1,

		// Sector's posY that these edges belongs to
		posY: Int = 1,
		topLeftCorner: TopLeftCorner = Corner.topLeft(true),
		topRightCorner: TopRightCorner = Corner.topRight(true),
		bottomLeftCorner: BottomLeftCorner = Corner.bottomLeft(true),
		bottomRightCorner: BottomRightCorner = Corner.bottomRight(true),
		topSide: TopSide = Side.top(BitSet(1, 5, 9)),
		bottomSide: BottomSide = Side.bottom(BitSet(2, 6, 8)),
		leftSide: LeftSide = Side.left(BitSet(1, 2, 3)),
		rightSide: RightSide = Side.right(BitSet(2, 4))
	) = Edges(
		posX,
		posY,
		topLeftCorner,
		topRightCorner,
		bottomLeftCorner,
		bottomRightCorner,
		topSide,
		bottomSide,
		leftSide,
		rightSide
	)
}
