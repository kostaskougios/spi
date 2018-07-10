package com.aktit.sql

import org.apache.spark.internal.Logging
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.language.postfixOps

object AppendJob extends Logging
{
	def main(args: Array[String]): Unit = {

		val data1 = for (i <- 1 to 10) yield Person(i, s"text for row $i")

		val spark = SparkSession
			.builder
			.master("local[4]")
			.getOrCreate

		// For implicit conversions like converting RDDs to DataFrames
		import spark.implicits._

		val df = data1.toDF
		df.printSchema()

		df.createOrReplaceTempView("people")

		spark.sql("select * from people where id % 2 == 0").show()
		df.write
			.mode(SaveMode.Append)
			.save("/tmp/AppendJob")

	}
}

case class Person(id: Long, name: String)