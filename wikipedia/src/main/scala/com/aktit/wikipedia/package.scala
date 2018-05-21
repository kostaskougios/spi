package com.aktit

import com.aktit.dto.EpochDateTime
import com.aktit.wikipedia.dto._
import org.apache.spark.SparkConf

/**
  * @author kostas.kougios
  *         21/05/18 - 21:58
  */
package object wikipedia
{
	// Get the SparkConf for the wikipedia jobs
	def wikipediaSparkConf: SparkConf = new SparkConf()
		// note this doesn't affect sc.objectFile or saveAsObjectFile, those still use java serialization
		.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
		.registerKryoClasses(Array(
			classOf[Page],
			classOf[Revision],
			classOf[EpochDateTime],
			classOf[ContributorUser],
			classOf[ContributorIP],
			classOf[ContributorUnknown]
		)
		)

}
