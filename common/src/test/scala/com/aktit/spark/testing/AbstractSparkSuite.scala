package com.aktit.spark.testing

import java.util.UUID

import com.aktit.utils.GuiceApp
import com.google.inject.Module
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Dataset, SparkSession}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

/**
  * Base test case for spark tests
  *
  * @author kostas.kougios
  */
abstract class AbstractSparkSuite extends FunSuite with Matchers with BeforeAndAfterAll
{

	protected val session = AbstractSparkSuite.session
	protected val sc = session.sparkContext

	protected def randomTmpDir = s"/tmp/${UUID.randomUUID}"

	protected def conf = new SparkConf().setAppName(getClass.getName).setMaster("local")

	protected def createApp(modules: Module*) = GuiceApp.app(session, modules: _*)

	implicit class DatasetImplicits[A](ds: Dataset[A])
	{
		def toSeq = ds.collect.toSeq

		def toSet = ds.collect.toSet
	}

}

object AbstractSparkSuite
{
	private val session = SparkSession.builder
		.master("local[*]")
		.config("spark.ui.enabled", false)
		.getOrCreate
}