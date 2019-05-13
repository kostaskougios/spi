package com.aktit.sql.performance

import org.apache.spark.internal.Logging
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

/**
  * Stores data into targetDir in all formats required for the benchmark.
  *
  * @author kostas.kougios
  *         11/07/18 - 13:46
  */
class Creator[A <: Product : TypeTag : ClassTag](spark: SparkSession, targetDir: String, howMany: Long) extends Logging
{
	def create(testData: Long => A) = {
		// We need to create a lot of test data without running out of memory. So we group the data together (using iterators to avoid filling up the memory)
		// and append them to our target directories
		val rdd = spark.sparkContext.parallelize(1l to howMany).map(i => testData(i))

		logInfo("Creating dataframe")
		val df = spark.createDataFrame(rdd).toDF

		logInfo(s"Schema : ${df.schema}")

		logInfo("Storing ORC")
		df.toDF.write.mode(SaveMode.Overwrite).orc(s"$targetDir/orc")

		logInfo("Storing Avro")
		df.toDF.write.format("avro").mode(SaveMode.Overwrite).save(s"$targetDir/avro")

		logInfo("Storing Parquet")
		df.toDF.write.mode(SaveMode.Overwrite).parquet(s"$targetDir/parquet")

	}
}
