package com.aktit.testjob

import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author kostas.kougios
  */
object SqlTestJob extends Logging
{
	def main(args: Array[String]): Unit = {
		val conf = new SparkConf().setAppName(getClass.getName)
		val sc = new SparkContext(conf)

		// TODO: spark sql example
		try {
			val rdd = sc.parallelize(1 to 100000)
			rdd.map(_ * 2).foreach { i =>
				logInfo(s"[${Thread.currentThread}}] i is $i")
				Thread.sleep(1000)
			}
		} finally {
			sc.stop()
		}

	}
}
