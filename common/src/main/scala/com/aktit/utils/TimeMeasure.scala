package com.aktit.utils

/**
  * @author kostas.kougios
  *         26/05/18 - 23:45
  */
object TimeMeasure
{
	/**
	  * Measures how long f takes
	  *
	  * @param f the function to measure
	  * @tparam R the return type of f
	  * @return (dt : time in millis, R as returned by f)
	  */
	def dt[R](f: => R): (Long, R) = {
		val start = System.currentTimeMillis
		val r = f
		val dt = System.currentTimeMillis - start
		(dt, r)
	}
}
