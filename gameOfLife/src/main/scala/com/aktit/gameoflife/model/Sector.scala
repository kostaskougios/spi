package com.aktit.gameoflife.model

import scala.collection.immutable.BitSet

/**
  * A game universe contains many sectors.
  *
  * Universe :
  *
  * S(1,1) S(1,2) S(1,3) ...
  * S(2,1) S(2,2) S(2,3) ...
  * ....
  *
  * This is a trait. We may have multiple implementations.
  *
  * @author kostas.kougios
  *         25/05/18 - 20:26
  */
trait Sector extends Serializable
{
	def withBoundaries(boundaries: Boundaries): Sector

	// position within the universe of sectors
	def posX: Int

	// position within the universe of sectors
	def posY: Int

	def width: Int

	def height: Int

	def boundaries: Boundaries

	def matrix: Matrix

	def isLive(x: Int, y: Int): Boolean

	def liveNeighbours(x: Int, y: Int): Int = {
		// this needs to be as fast as possible
		var n = 0
		if (isLive(x - 1, y - 1)) n += 1
		if (isLive(x, y - 1)) n += 1
		if (isLive(x + 1, y - 1)) n += 1
		if (isLive(x - 1, y)) n += 1
		if (isLive(x + 1, y)) n += 1
		if (isLive(x - 1, y + 1)) n += 1
		if (isLive(x, y + 1)) n += 1
		if (isLive(x + 1, y + 1)) n += 1
		n
	}

	/**
	  * Plays 1 turn of the game of life.
	  *
	  * Rules (https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)
	  *
	  * The universe of the Game of Life is an infinite, two-dimensional orthogonal grid of square cells, each of which
	  * is in one of two possible states, alive or dead, (or populated and unpopulated, respectively). Every cell
	  * interacts with its eight neighbours, which are the cells that are horizontally, vertically, or diagonally
	  * adjacent. At each step in time, the following transitions occur:
	  *
	  * Any live cell with fewer than two live neighbors dies, as if by under population.
	  * Any live cell with two or three live neighbors lives on to the next generation.
	  * Any live cell with more than three live neighbors dies, as if by overpopulation.
	  * Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
	  *
	  * The initial pattern constitutes the seed of the system. The first generation is created by applying the above
	  * rules simultaneously to every cell in the seed; births and deaths occur simultaneously, and the discrete moment
	  * at which this happens is sometimes called a tick. Each generation is a pure function of the preceding one.
	  * The rules continue to be applied repeatedly to create further generations.
	  *
	  * @return the modified Sector
	  */
	def evolve: Sector = {
		// this method needs to be optimized for performance and memory usage
		val b = Matrix.newBuilder(width, height)
		for {y <- (0 until height).par} { // note the Matrix builder doesn't need sync if we parallelise on y. Also we won't use more memory.
			for {x <- 0 until width} {
				val n = liveNeighbours(x, y)
				val live = isLive(x, y)
				if ((live && (n == 2 || n == 3)) || (!live && n == 3)) b += (x, y)
			}
		}
		Sector(posX, posY, b.result(), Boundaries.empty(width, height))
	}

	/**
	  * Convert to ascii art.
	  */
	def toAscii: String = {
		val top = for (x <- -1 to width) yield if (isLive(x, -1)) "▒" else "░"
		val bottom = for (x <- -1 to width) yield if (isLive(x, height)) "▒" else "░"
		top.mkString + "\n" +
			(0 until height).map {
				y =>
					(if (isLive(-1, y)) "▒" else "░") + (0 until width).map {
						x =>
							if (isLive(x, y)) '█' else ' '
					}.mkString + (if (isLive(width, y)) "▒" else "░")
			}.mkString("\n") + "\n" + bottom.mkString
	}

	def edges: Edges = Edges(
		posX,
		posY,
		Corner.topLeft(isLive(0, 0)),
		Corner.topRight(isLive(width - 1, 0)),
		Corner.bottomLeft(isLive(0, height - 1)),
		Corner.bottomRight(isLive(width - 1, height - 1)),
		Side.top(BitSet((for (x <- 0 until width if isLive(x, 0)) yield x): _*)),
		Side.bottom(BitSet((for (x <- 0 until width if isLive(x, height - 1)) yield x): _*)),
		Side.left(BitSet((for (y <- 0 until height if isLive(0, y)) yield y): _*)),
		Side.right(BitSet((for (y <- 0 until height if isLive(width - 1, y)) yield y): _*))
	)
}

object Sector
{
	def apply(posX: Int, posY: Int, width: Int, height: Int, liveCoordinates: Seq[(Int, Int)], boundaries: Boundaries): Sector = {
		if (boundaries.width != width) throw new IllegalArgumentException("boundaries.width!=width")
		if (boundaries.height != height) throw new IllegalArgumentException("boundaries.height!=height")

		val matrix = Matrix(width, height, liveCoordinates)
		StdSector(posX, posY, matrix, boundaries)
	}

	def apply(posX: Int, posY: Int, matrix: Matrix, boundaries: Boundaries): Sector = StdSector(posX, posY, matrix, boundaries)

	private case class StdSector(posX: Int, posY: Int, matrix: Matrix, boundaries: Boundaries) extends Sector
	{
		if (matrix.width != boundaries.width) throw new IllegalArgumentException(s"matrix.width != boundaries.width : ${matrix.width}!=${boundaries.width}")
		if (matrix.height != boundaries.height) throw new IllegalArgumentException(s"matrix.height != boundaries.height : ${matrix.height}!=${boundaries.height}")

		override def isLive(x: Int, y: Int) = {
			if (x < -1 || x > matrix.width) throw new IllegalArgumentException(s"x is invalid : $x")
			if (y < -1 || y > matrix.height) throw new IllegalArgumentException(s"y is invalid : $y")

			if (y == -1) boundaries.isTopLive(x)
			else if (y == matrix.height) boundaries.isBottomLive(x)
			else if (x == -1) boundaries.isLeftLive(y)
			else if (x == matrix.width) boundaries.isRightLive(y)
			else matrix.isLive(x, y)
		}

		override def width = matrix.width

		override def height = matrix.height

		override def withBoundaries(newBoundaries: Boundaries) = StdSector(posX, posY, matrix, newBoundaries)
	}

}
