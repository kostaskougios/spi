package com.aktit.landregistry

import com.aktit.landregistry.schema.LandRegistry
import org.apache.spark.internal.Logging
import org.apache.spark.sql.{Encoders, SparkSession}

import scala.language.postfixOps

/**
  * This job will find the most expensive property since 1995 in Bromley's BR2 postcode.
  *
  * Get the source data file from
  *
  * http://prod1.publicdata.landregistry.gov.uk.s3-website-eu-west-1.amazonaws.com/pp-complete.csv
  * (4GB)
  *
  * Run it with something like:
  *
  * -Dspark.master=local[*] -Dspark.src=/home/ariskk/big-data/land-registry/pp-complete.csv
  *
  * @author kostas.kougios
  */
object LandRegistryJob extends Logging
{
	def main(args: Array[String]): Unit = {

		val spark = SparkSession
			.builder
			.getOrCreate

		// the source file, should point to pp-complete.csv
		val src = spark.conf.get("spark.src")

		// For implicit conversions like converting RDDs to DataFrames
		import spark.implicits._

		val ds = spark.read
			// Normally we would get columns like _c0, _c1 ...etc but we can have something more
			// meaningful if we use a case class. We also can define types of columns
			.schema(Encoders.product[LandRegistry].schema)
			.csv(src)
			.as[LandRegistry] // Dataset[LandRegistry]
		// show the schema and sample rows
		ds.show()

		// find the most expensive properties for my postcode.
		ds.select($"postCode", $"price", $"purchasedDate")
			.filter($"postCode".startsWith("BR2"))
			.sort($"price" desc)
			.show()

		for (row <- ds.take(10)) println(row)
	}
}
