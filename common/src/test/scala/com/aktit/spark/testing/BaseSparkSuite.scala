package com.aktit.spark.testing

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

/**
  * Base test case for spark tests
  *
  * @author kostas.kougios
  */
abstract class BaseSparkSuite extends FunSuite with Matchers with BeforeAndAfterAll
{

	val sc = new SparkContext(conf)

	protected def conf = new SparkConf().setAppName(getClass.getName).setMaster("local")

	override protected def afterAll(): Unit = {
		super.afterAll()
		sc.stop()
	}

}
