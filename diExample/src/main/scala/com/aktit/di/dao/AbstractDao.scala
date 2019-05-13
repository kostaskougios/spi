package com.aktit.di.dao

import org.apache.spark.sql.{Dataset, Encoders, SaveMode, SparkSession}

import scala.reflect.runtime.universe.TypeTag

/**
  * @author kostas.kougios
  *         13/05/19 - 11:15
  */
abstract class AbstractDao[A <: Product : TypeTag](spark: SparkSession, path: String)
{
	private implicit val encoder = Encoders.product[A]

	def read: Dataset[A] = spark.read.orc(path).as[A]

	def append(ds: Dataset[A]): Unit = ds.write.mode(SaveMode.Append).orc(path)
}
