package com.aktit.gameoflife.spark

import org.apache.spark.SparkContext

/**
  * @author kostas.kougios
  *         27/05/18 - 20:53
  */
trait Command
{
	def run(sc: SparkContext, out: String): Unit
}
