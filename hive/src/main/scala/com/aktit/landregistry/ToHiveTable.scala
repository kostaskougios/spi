package com.aktit.landregistry

import org.apache.hadoop.hive.conf.HiveConf
import org.apache.spark.internal.Logging
import org.apache.spark.sql._

/** @author
  *   kostas.kougios 24/07/18 - 08:19
  */
object ToHiveTable extends Logging {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName("Spark Hive Example")
      .config("javax.jdo.option.ConnectionURL", "jdbc:postgresql://server.lan/hive")
      .config("javax.jdo.option.ConnectionDriverName", "org.postgresql.Driver")
      .config("javax.jdo.option.ConnectionUserName", "hive")
      .config("javax.jdo.option.ConnectionPassword", "123123")
      .config(HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION_RECORD_VERSION.varname, false)
      .enableHiveSupport()
      .getOrCreate()

    import spark.sql

    // create table if not exists sparktest(id int,name string) stored as orc;
    spark
      .createDataFrame {
        for (i <- 1 to 100) yield (i, s"row $i")
      }
      .write
      .insertInto("sparktest")

    sql("select * from sparktest").show()
  }
}
