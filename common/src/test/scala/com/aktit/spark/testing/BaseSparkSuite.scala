package com.aktit.spark.testing

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

/**
  * Base test case for spark tests
  *
  * @author kostas.kougios
  */
abstract class BaseSparkSuite extends FunSuite with Matchers with BeforeAndAfterAll
{

	protected val session = BaseSparkSuite.session
	protected val sc = session.sparkContext

	protected def conf = new SparkConf().setAppName(getClass.getName).setMaster("local")
}

object BaseSparkSuite
{
	private val session = SparkSession.builder.master("local[*]").getOrCreate
}