package com.aktit.di.dao

import com.aktit.di.config.AppConfig
import com.aktit.di.model.Account
import javax.inject.{Inject, Singleton}
import org.apache.spark.sql.SparkSession

/**
  * @author kostas.kougios
  *         13/05/19 - 17:49
  */
@Singleton
class AccountDao @Inject()(spark: SparkSession, appConfig: AppConfig)
	extends AbstractDao[Account](spark, appConfig.rootPath + "/account")
