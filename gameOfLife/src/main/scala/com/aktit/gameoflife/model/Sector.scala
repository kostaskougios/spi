package com.aktit.gameoflife.model

/**
  * @author kostas.kougios
  *         25/05/18 - 20:26
  */
trait Sector
{
	def width: Int

	def height: Int

	def boundaries: Boundaries

	def matrix: Matrix

	def isLive(x: Int, y: Int): Boolean

	def liveNeighbours(x: Int, y: Int): Int = {
		val live = for {
			i <- x - 1 to x + 1
			j <- y - 1 to y + 1 if (i != x || j != y) && isLive(i, j)
		} yield 1
		live.size
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
		val newAlive = for {
			x <- 0 until width
			y <- 0 until height
			n = liveNeighbours(x, y)
			live = isLive(x, y)
			if (live && (n == 2 || n == 3)) || (!live && n == 3)
		} yield (x, y)
		Sector(width, height, newAlive, boundaries)
	}

	def toAscii: String = (0 until height).map {
		y =>
			(0 until width).map {
				x =>
					if (isLive(x, y)) '█' else ' '
			}.mkString
	}.mkString("\n")
}

object Sector
{
	def apply(width: Int, height: Int, liveCoordinates: Seq[(Int, Int)], boundaries: Boundaries): Sector = {
		if (boundaries.width != width) throw new IllegalArgumentException("boundaries.width!=width")
		if (boundaries.height != height) throw new IllegalArgumentException("boundaries.height!=height")

		val matrix = Matrix(width, height, liveCoordinates)
		StdSector(matrix, boundaries)
	}

	def apply(matrix: Matrix, boundaries: Boundaries): Sector = StdSector(matrix, boundaries)

	private case class StdSector(matrix: Matrix, boundaries: Boundaries) extends Sector
	{
		if (matrix.width != boundaries.width) throw new IllegalArgumentException(s"matrix.width != boundaries.width : ${matrix.width}!=${boundaries.width}")
		if (matrix.height != boundaries.height) throw new IllegalArgumentException(s"matrix.height != boundaries.height : ${matrix.height}!=${boundaries.height}")

		override def isLive(x: Int, y: Int) = {
			if (x < -1 || x > matrix.width) throw new IllegalArgumentException(s"x is invalid : $x")
			if (y < -1 || y > matrix.height) throw new IllegalArgumentException(s"y is invalid : $y")

			if (y == -1) boundaries.isTop(x)
			else if (y == matrix.height) boundaries.isBottom(x)
			else if (x == -1) boundaries.isLeftLive(y)
			else if (x == matrix.width) boundaries.isRightLive(y)
			else matrix.isLive(x, y)
		}

		override def width = matrix.width

		override def height = matrix.height
	}

}
