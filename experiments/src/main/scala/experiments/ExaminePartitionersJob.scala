package experiments

import org.apache.spark.{SparkConf, SparkContext}

object ExaminePartitionersJob
{
	def main(args: Array[String]): Unit = {

		val conf = new SparkConf().setAppName(getClass.getName)
			.setMaster("local[4]")
			.set("spark.hadoop.validateOutputSpecs", "false")
		val sc = new SparkContext(conf)

		try {
			val status = sc.statusTracker
			val rdd = sc.parallelize(1 to 32).groupBy(_ % 2 == 0)
			println(rdd.partitioner)
			println(rdd.toDebugString)

			rdd.saveAsObjectFile("/tmp/f1")
			val loaded = sc.objectFile[(Boolean, Iterable[Int])]("/tmp/f1")
			println(loaded.partitioner)

		} finally {
			sc.stop()
		}
	}

}
