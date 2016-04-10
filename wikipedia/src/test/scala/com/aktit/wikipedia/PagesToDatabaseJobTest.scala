package com.aktit.wikipedia

import java.sql.Timestamp

import com.aktit.spark.testing.BaseSparkSuite
import com.aktit.testconfig.Config
import org.apache.spark.sql.SQLContext
import org.joda.time.DateTime
import scalikejdbc._

import scala.util.Try

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
class PagesToDatabaseJobTest extends BaseSparkSuite
{

	import Data._
	import PagesToDatabaseJob._

	ConnectionPool.singleton(Config.PhoenixJdbcUrl, "", "")
	implicit val session = AutoSession
	val sqlContext = new SQLContext(sc)

	test("preparePageTable") {
		preparePageTable(sc.parallelize(Seq(Page1a, Page2))).collect().toSet should be(Set(
			(1, "en", "page1", "redirect1"),
			(2, "en", "page2", null)
		))
	}

	test("prepareContributorsTable") {
		prepareContributorsTable(sc.parallelize(Seq(Page1a, Page2))).collect().toSet should be(Set(
			(100, "kostas"),
			(102, "nick")
		))
	}

	test("prepareRevisionsTable") {
		val p1r = Page1a.revisions.head
		val p2r = Page2.revisions.head
		prepareRevisionsTable(sc.parallelize(Seq(Page1a, Page2))).collect().toSet should be(Set(
			(p1r.id, p1r.parentId, p1r.time, p1r.comment, p1r.model, p1r.format, p1r.text, p1r.sha1),
			(p2r.id, p2r.parentId, p2r.time, p2r.comment, p2r.model, p2r.format, p2r.text, p2r.sha1)
		))
	}

	test("populatePagesTable") {
		val TestTable = "pagestest"
		prepareTable(TestTable, SQL("create table " + TestTable + "(id bigint not null,lang varchar not null,title varchar, redirect varchar, constraint PK_PAGETEST primary key (id,lang))")) {
			val data = sc.parallelize(Seq(Page1a, Page2))
			populatePagesTable(Config.Zookeeper, TestTable, data)
			val loaded = sqlContext.read
				.options(Map("table" -> TestTable, "zkUrl" -> Config.Zookeeper))
				.format("org.apache.phoenix.spark")
				.load()
				.map { row =>
					(row.getLong(0), row.getString(1), row.getString(2), row.getString(3))
				}.collect().toSet
			loaded should be(preparePageTable(data).collect().toSet)
		}
	}

	test("populateContributorsTable") {
		val TestTable = "contributorstest"
		prepareTable(TestTable, SQL("create table " + TestTable + "(revision_id bigint primary key,name varchar)")) {
			val data = sc.parallelize(Seq(Page1a, Page2))
			populateContributorsTable(Config.Zookeeper, TestTable, data)
			val loaded = sqlContext.read
				.options(Map("table" -> TestTable, "zkUrl" -> Config.Zookeeper))
				.format("org.apache.phoenix.spark")
				.load()
				.map { row =>
					(row.getLong(0), row.getString(1))
				}.collect().toSet
			loaded should be(prepareContributorsTable(data).collect().toSet)
		}
	}

	test("populateRevisionsTable") {
		val TestTable = "revisionstest"
		prepareTable(TestTable, SQL("create table " + TestTable + "(id bigint primary key,PARENTID bigint,TIME timestamp,COMMENT varchar,MODEL varchar,FORMAT varchar,TEXT varchar,SHA1 varchar)")) {
			val data = sc.parallelize(Seq(Page1a, Page2))
			populateRevisionsTable(Config.Zookeeper, TestTable, data)
			val loaded = sqlContext.read
				.options(Map("table" -> TestTable, "zkUrl" -> Config.Zookeeper))
				.format("org.apache.phoenix.spark")
				.load()
				.map { row =>
					(row.getLong(0), row.getLong(1), new DateTime(row.getAs[Timestamp](2)), row.getString(3), row.getString(4), row.getString(5), row.getString(6), row.getString(7))
				}.collect().toSet
			loaded should be(prepareRevisionsTable(data).collect().toSet)
		}
	}

	def prepareTable(table: String, ddl: SQL[Nothing, NoExtractor])(f: => Unit): Unit = {
		Try(SQL(s"drop table $table").executeUpdate().apply())
		ddl.executeUpdate().apply()
		try {
			f
		} finally {
			Try(SQL(s"drop table $table").executeUpdate().apply())
		}
	}
}
