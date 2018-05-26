package com.aktit.optimized

/**
  * @author kostas.kougios
  *         27/05/18 - 00:17
  */
object Tuples
{
	def tupleField1ToIntArray(s: Seq[(Int, _)]): Array[Int] = {
		val a = new Array[Int](s.size)
		var i = 0
		val it = s.iterator
		while (it.hasNext) {
			a(i) = it.next()._1
			i += 1
		}
		a
	}
}
