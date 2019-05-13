package com.aktit.di.dao

import com.aktit.di.config.AppConfig
import com.aktit.di.model.Transfer
import javax.inject.{Inject, Singleton}
import org.apache.spark.sql.SparkSession

/**
  * @author kostas.kougios
  *         13/05/19 - 11:20
  */
@Singleton
class TransferDao @Inject()(spark: SparkSession, appConfig: AppConfig)
	extends AbstractDao[Transfer](spark, appConfig.rootPath + "/transfer")
