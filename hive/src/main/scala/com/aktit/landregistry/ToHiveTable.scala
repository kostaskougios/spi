package com.aktit.landregistry

import org.apache.spark.internal.Logging
import org.apache.spark.sql._

/**
  * @author kostas.kougios
  *         24/07/18 - 08:19
  */
object ToHiveTable extends Logging
{
	def main(args: Array[String]): Unit = {

		val spark = SparkSession
			.builder
			.appName("Spark Hive Example")
			.config("spark.sql.warehouse.dir", "/tmp/warehouse")
			.enableHiveSupport
			.getOrCreate

		import spark.sql

		sql("CREATE TABLE IF NOT EXISTS src (key INT, value STRING) USING hive")

		sql("SELECT COUNT(*) FROM src").show()
	}
}
