package com.aktit.di

import com.aktit.di.config.{AppConfig, DiModule}
import com.aktit.spark.testing.AbstractSparkSuite

/**
  * @author kostas.kougios
  *         13/05/19 - 11:26
  */
class AbstractDiSuite extends AbstractSparkSuite
{
	protected def createDiApp = createApp(new DiModule(AppConfig(randomTmpDir)))

}
