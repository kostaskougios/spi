package com.aktit.gameoflife.spark

import org.apache.spark.SparkContext

/**
  * @author kostas.kougios
  *         27/05/18 - 20:53
  */
trait Command extends Serializable /* Serializable is a spark requirement */
{
	def run(sc: SparkContext, out: String): Unit
}
