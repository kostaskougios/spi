package com.aktit.sql.performance

import com.databricks.spark.avro._
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
class Creator[A <: Product : TypeTag : ClassTag](spark: SparkSession, testData: Iterator[A], targetDir: String, group: Int) extends Logging
{
	def create() = {
		// We need to create a lot of test data without running out of memory. So we group the data together (using iterators to avoid filling up the memory)
		// and append them to our target directories
		for ((data, grp) <- testData.grouped(group).zipWithIndex) {

			logInfo(s"parallelizing test data group $grp")

			val rdd = spark.sparkContext.parallelize(data)

			logInfo("Creating dataframe")
			val df = spark.createDataFrame(rdd).toDF

			logInfo(s"Schema : ${df.schema}")

			logInfo("Storing ORC")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).orc(s"$targetDir/orc")

			logInfo("Storing Avro")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).avro(s"$targetDir/avro")

			logInfo("Storing Parquet")
			df.toDF.write.mode(if (grp == 0) SaveMode.Overwrite else SaveMode.Append).parquet(s"$targetDir/parquet")

		}

	}
}