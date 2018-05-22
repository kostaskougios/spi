package com.aktit.landregistry

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

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
  * -Dspark.master=local[4] -Dspark.src=/home/ariskk/big-data/land-registry/pp-complete.csv
  *
  * @author kostas.kougios
  */
object LandRegistryJob extends Logging
{
	def main(args: Array[String]): Unit = {

		val spark = SparkSession
			.builder
			.getOrCreate

		val src = spark.conf.get("spark.src")
		// For implicit conversions like converting RDDs to DataFrames
		import spark.implicits._

		val df = spark.read
			.schema(StructType(
				Seq(
					StructField("id", StringType),
					StructField("price", IntegerType),
					StructField("purchasedDate", DateType),
					StructField("postCode", StringType),
					StructField("propertyType", StringType),
					StructField("unknown1", StringType),
					StructField("unknown2", StringType),
					StructField("houseNumber", StringType),
					StructField("houseName", StringType),
					StructField("address1", StringType),
					StructField("address2", StringType),
					StructField("address3", StringType),
					StructField("address4", StringType),
					StructField("address5", StringType)
				)
			))
			.csv(src)
		df.show()

		df.select($"postCode", $"price", $"purchasedDate")
			.filter($"postCode".startsWith("BR2"))
			.sort($"price" desc)
			.show()

	}
}
