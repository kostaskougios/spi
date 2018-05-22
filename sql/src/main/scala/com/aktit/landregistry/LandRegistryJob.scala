package com.aktit.landregistry

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

import scala.language.postfixOps

/**
  * Get the source data file from
  *
  * http://prod1.publicdata.landregistry.gov.uk.s3-website-eu-west-1.amazonaws.com/pp-complete.csv
  * (4GB)
  *
  * It is not that big but will do.
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
			.withColumnRenamed("_c9", "address1")
			.withColumnRenamed("_c10", "address2")
			.withColumnRenamed("_c11", "address3")
			.withColumnRenamed("_c12", "address4")
			.withColumnRenamed("_c13", "address5")
		df.show()

		df.select($"postCode", $"price")
			.filter($"postCode".startsWith("BR2"))
			.sort($"price" desc)
			.show()

	}
}
