package com.aktit.gameoflife.model

/**
  * @author kostas.kougios
  *         25/05/18 - 20:26
  */
trait Sector
{
	def isLive(x: Int, y: Int): Boolean

	def liveNeighbours(x: Int, y: Int): Int
}

object Sector
{

	private case class DenseSector(matrix: Matrix, boundaries: Boundaries) extends Sector
	{
		override def liveNeighbours(x: Int, y: Int): Int = {
			val live = for {
				i <- x - 1 to x + 1
				j <- y - 1 to y + 1 if isLive(i, j)
			} yield 1
			live.size
		}

		override def isLive(x: Int, y: Int) = {
			if (x < -1 || x > matrix.width + 1) throw new IllegalArgumentException(s"x is invalid : $x")
			if (y < -1 || y > matrix.height + 1) throw new IllegalArgumentException(s"y is invalid : $y")

			if (y == -1) boundaries.isTop(x)
			else if (y == matrix.height + 1) boundaries.isBottom(x)
			else if (x == -1) boundaries.isLeftLive(y)
			else if (x == matrix.width + 1) boundaries.isRightLive(y)
			else matrix.isLive(x, y)
		}
	}

}
