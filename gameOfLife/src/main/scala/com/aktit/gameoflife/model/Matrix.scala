package com.aktit.gameoflife.model

import scala.collection.immutable.BitSet
import scala.util.Random

/**
  * @author kostas.kougios
  *         25/05/18 - 22:20
  */
trait Matrix
{
	def width: Int

	def height: Int

	def isLive(x: Int, y: Int): Boolean
}

object Matrix
{
	def apply(width: Int, height: Int, liveCoordinates: Seq[(Int, Int)]): Matrix = {
		val bitSetMap = liveCoordinates.groupBy(_._2).map {
			case (y, coords) => (y, BitSet(coords.map(_._1): _*))
		}
		val bitSets = new Array[BitSet](height)
		for (y <- 0 until height) if (bitSetMap.contains(y)) bitSets(y) = bitSetMap(y) else bitSets(y) = BitSet.empty

		BitSetMatrix(width, height, bitSets)
	}

	def random(width: Int, height: Int, howManyLive: Int): Matrix = apply(
		width,
		height,
		for (_ <- 1 to howManyLive) yield (Random.nextInt(width), Random.nextInt(height))
	)

	private case class BitSetMatrix(width: Int, height: Int, data: Array[BitSet]) extends Matrix
	{
		if (data.length != height) throw new IllegalArgumentException(s"expected array length to match the height of the matrix : ${data.length}!=$height")
		for (s <- data) if (s.nonEmpty && (s.min < 0 || s.max > width)) throw new IllegalArgumentException(s"Invalid x coordinates ${s.min} to ${s.max}, should have been between 0 and $width")

		override def isLive(x: Int, y: Int): Boolean = {
			if (x < 0 || x > width) throw new IllegalArgumentException(s"x is out of bounds : $x")
			if (y < 0 || y > height) throw new IllegalArgumentException(s"y is out of bounds : $y")
			data(y)(x)
		}
	}

}