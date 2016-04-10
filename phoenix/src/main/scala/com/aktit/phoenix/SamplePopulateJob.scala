package com.aktit.phoenix

/**
  * CREATE TABLE OUTPUT_TEST_TABLE (id BIGINT NOT NULL PRIMARY KEY, col1 VARCHAR, col2 INTEGER) SALT_BUCKETS = 8;
  *
  * @author kostas.kougios
  */
object SamplePopulateJob extends Logging
{
	def main(args: Array[String]): Unit = {
		val Divider = 4096

		val conf = new SparkConf().setAppName(getClass.getName)
		val hbaseZookeeper = conf.get("spark.hbase.zookeeper")
		val numOfRows = conf.getLong("spark.num-of-rows", 10)

		val sc = new SparkContext(conf)

		try {

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