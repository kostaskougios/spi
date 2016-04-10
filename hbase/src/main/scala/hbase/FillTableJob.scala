package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.io.LongWritable
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Fills an hbase nosql table with key/values. For phoenix sql tables please see the phoenix project
  *
  * @author kostas.kougios
  */
object FillTableJob
{
	def main(args: Array[String]): Unit = {

		val DKey = 4096l
		val Max = 5000000000l
		val conf = new SparkConf().setAppName(getClass.getName)
		val sc = new SparkContext(conf)

		try {

			val rdd = sc.parallelize(1l to Max / DKey)
			val hConf = hbaseConfig
			hConf.set(TableOutputFormat.OUTPUT_TABLE, "spark_test")

			val jobConf = new Configuration(hConf)
			jobConf.set("mapreduce.outputformat.class", classOf[TableOutputFormat[LongWritable]].getName)

			rdd.flatMap {
				i =>
					(i * DKey until (i + 1) * DKey).map {
						id =>
							val put = new Put(s"$id key xxxx".getBytes)
							// put an int into cf:sample-int
							put.addColumn("cf".getBytes, "sample-int".getBytes, s"a really big string with value $id".getBytes)

							('x', put)
					}
			}.saveAsNewAPIHadoopDataset(jobConf)
		} finally {
			sc.stop()
		}
	}

}
