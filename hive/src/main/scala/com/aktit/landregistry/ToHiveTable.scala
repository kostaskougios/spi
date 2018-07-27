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
			.config("javax.jdo.option.ConnectionURL", "jdbc:postgresql://server.lan/hive")
			.config("javax.jdo.option.ConnectionDriverName", "org.postgresql.Driver")
			.config("javax.jdo.option.ConnectionUserName", "hive")
			.config("javax.jdo.option.ConnectionPassword", "123123")
			.enableHiveSupport
			.getOrCreate

		import spark.sql

		sql("SELECT * FROM ratings").show()
	}
}
