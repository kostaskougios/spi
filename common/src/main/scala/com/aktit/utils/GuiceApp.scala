package com.aktit.utils

import com.google.inject.{AbstractModule, Guice, Injector, Module}
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.reflect.runtime.universe.TypeTag

/**
  * @author kostas.kougios
  *         13/05/19 - 11:04
  */
class GuiceApp private(injector: Injector)
{
	def instance[A: TypeTag]: A = injector.instance[A]
}

object GuiceApp
{
	def app(session: SparkSession, modules: Module*) = {
		val injector = Guice.createInjector((Seq(new SparkModule(session)) ++ modules).asJava)
		new GuiceApp(injector)
	}
}

class SparkModule(session: SparkSession) extends AbstractModule with ScalaModule
{
	override def configure() = {
		bind[SparkSession].toInstance(session)
	}
}
