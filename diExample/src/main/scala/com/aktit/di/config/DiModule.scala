package com.aktit.di.config

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

/**
  * @author kostas.kougios
  *         13/05/19 - 11:22
  */
class DiModule(appConfig: AppConfig) extends AbstractModule with ScalaModule
{
	override def configure() = {
		bind[AppConfig].toInstance(appConfig)
	}
}
