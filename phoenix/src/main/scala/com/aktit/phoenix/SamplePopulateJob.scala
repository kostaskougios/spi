package com.aktit.phoenix

import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Create this table in HBase :
  * CREATE TABLE OUTPUT_TEST_TABLE (id BIGINT NOT NULL PRIMARY KEY, col1 VARCHAR, col2 INTEGER) SALT_BUCKETS = 8;
  *
  * Then run this class with these jvm args:
  *
  * -Dspark.hbase.zookeeper=server.lan -Dspark.master=local[4] -Dspark.num-of-rows=1000000000
  *
  * @author kostas.kougios
  */
object SamplePopulateJob extends Logging
{
	def main(args: Array[String]): Unit = {
		val Divider = 4096

		val conf = new SparkConf().setAppName(getClass.getName)
		val hbaseZookeeper = conf.get("spark.hbase.zookeeper")
		val numOfRows = conf.getLong("spark.num-of-rows", 8192)

		val sc = new SparkContext(conf)

		try {
			import org.apache.phoenix.spark._

			val seq = for (i <- 1l to numOfRows by numOfRows / Divider) yield i

			sc.parallelize(seq).flatMap {
				k =>
					logInfo(s"at $k")
					val it = (k to (k + numOfRows / Divider)).iterator.map(i => (i, s"row $i", i % (1024 * 1024)))
					it
			}.saveToPhoenix(
				"OUTPUT_TEST_TABLE",
				Seq("ID", "COL1", "COL2"),
				zkUrl = Some(hbaseZookeeper)
			)
		} finally {
			sc.stop()
		}

	}
}