package com.aktit.gameoflife.model

import com.aktit.optimized.Tuples

import java.util.concurrent.ThreadLocalRandom
import scala.collection.immutable.BitSet
import scala.collection.mutable

/** @author
  *   kostas.kougios 25/05/18 - 22:20
  */
trait Matrix extends Serializable {
  def width: Int

  def height: Int

  def isLive(x: Int, y: Int): Boolean
}

object Matrix {
  def apply(width: Int, height: Int, liveCoordinates: Seq[(Int, Int)]): Matrix = {
    val bitSetMap = liveCoordinates.groupBy(_._2).map { case (y, coords) =>
      (y, BitSet( /* avoid Int boxing */ Tuples.tupleField1ToIntArray(coords).toIndexedSeq: _*))
    }

    val bitSets = new Array[BitSet](height)
    for (y <- 0 until height) if (bitSetMap.contains(y)) bitSets(y) = bitSetMap(y) else bitSets(y) = BitSet.empty

    new BitSetMatrix(width, height, bitSets)
  }

  def apply(width: Int, height: Int, bitSets: Array[BitSet]): Matrix = new BitSetMatrix(width, height, bitSets)

  private class BitSetMatrix(val width: Int, val height: Int, data: Array[BitSet]) extends Matrix {
    if (data.length != height) throw new IllegalArgumentException(s"expected array length to match the height of the matrix : ${data.length}!=$height")
    for (s <- data)
      if (s.nonEmpty && (s.min < 0 || s.max > width))
        throw new IllegalArgumentException(s"Invalid x coordinates ${s.min} to ${s.max}, should have been between 0 and $width")

    override def isLive(x: Int, y: Int): Boolean = {
      if (x < 0 || x > width) throw new IllegalArgumentException(s"x is out of bounds : $x")
      if (y < 0 || y > height) throw new IllegalArgumentException(s"y is out of bounds : $y")
      val b = data(y)
      b.contains(x) // avoid b.apply due to Int boxing
    }

    // case classes don't correctly compare Array's
    override def equals(obj: scala.Any): Boolean = obj match {
      case o: Matrix =>
        for (x <- 0 until width)
          for (y <- 0 until height) if (o.isLive(x, y) != isLive(x, y)) return false
        true
      case _ => false
    }
  }

  def newBuilder(width: Int, height: Int): Builder = new Builder(width, height)

  class Builder private[Matrix] (width: Int, height: Int) {
    private val data = new Array[mutable.BitSet](height)
    for (y <- 0 until height) data(y) = new mutable.BitSet(width)

    def +=(x: Int, y: Int): Unit = {
      data(y) += x
    }

    def addRandomLiveCells(n: Int): Builder = {
      // this has to be as fast as possible.
      val r = ThreadLocalRandom.current()
      var i = 1
      while (i <= n) {
        +=(r.nextInt(width), r.nextInt(height))
        i += 1
      }
      this
    }

    def result(): Matrix = new BitSetMatrix(width, height, data.map(b => BitSet.fromBitMask(b.toBitMask)))
  }

}
