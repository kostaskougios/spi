package com.aktit.di.dao

import com.aktit.di.AbstractDiSuite
import com.aktit.di.config.AppConfig
import org.apache.spark.sql.SparkSession

import javax.inject.{Inject, Singleton}

/** @author
  *   kostas.kougios 13/05/19 - 17:41
  */
class AbstractDaoTest extends AbstractDiSuite {

  import session.implicits._

  test("append/read") {
    new App {
      val a1 = AD(1)
      val a2 = AD(2)
      dao.append(Seq(a1, a2).toDS())
      dao.read.toSet should be(Set(a1, a2))
    }
  }

  test("multiple appends") {
    new App {
      val a1 = AD(10)
      val a2 = AD(20)
      dao.append(Seq(a1).toDS())
      dao.read.toSet should be(Set(a1))
      dao.append(Seq(a2).toDS())
      dao.read.toSet should be(Set(a1, a2))
    }
  }

  class App {
    val app = createDiApp
    val dao = app.instance[ADao]
  }

}

case class AD(id: Int)

@Singleton
class ADao @Inject() (spark: SparkSession, appConfig: AppConfig) extends AbstractDao[AD](spark, appConfig.rootPath + "/adao")
