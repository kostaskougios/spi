package com.aktit.wikipedia

import com.aktit.wikipedia.dto.{ContributorUser, Page}
import org.apache.phoenix.spark._
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
/**
  * Inserts Page's into hbase/phoenix database.
  *
  * Run ddl.sql against hbase before running this job.
  *
  * @author kostas.kougios
  */
object PagesToDatabaseJob extends Logging
{

	def main(args: Array[String]): Unit = {

		val conf = wikipediaSparkConf.setAppName(getClass.getName)
		val src = conf.get("spark.src")
		val zookeeper = conf.get("spark.hbase.zookeeper")

		val sc = new SparkContext(conf)

		try {
			val pages = sc.objectFile[Page](src)
			populatePagesTable(zookeeper, "pages", pages)
			populateContributorsTable(zookeeper, "contributors", pages)
			populateRevisionsTable(zookeeper, "revisions", pages)
		} finally {
			sc.stop()
		}
	}

	def populateContributorsTable(zookeeper: String, tableName: String, pages: RDD[Page]): Unit = {
		prepareContributorsTable(pages).saveToPhoenix(
			tableName,
			Seq("REVISION_ID", "NAME"),
			zkUrl = Some(zookeeper)
		)
	}

	def populatePagesTable(zookeeper: String, tableName: String, pages: RDD[Page]): Unit =
		preparePageTable(pages).saveToPhoenix(
			tableName,
			Seq("ID", "LANG", "TITLE", "REDIRECT"),
			zkUrl = Some(zookeeper)
		)

	def populateRevisionsTable(zookeeper: String, tableName: String, pages: RDD[Page]): Unit = {
		prepareRevisionsTable(pages).saveToPhoenix(
			tableName,
			Seq("ID", "PARENTID", "TIME", "COMMENT", "MODEL", "FORMAT", "TEXT", "SHA1"),
			zkUrl = Some(zookeeper)
		)
	}

	def preparePageTable(pages: RDD[Page]) = pages.map(p => (p.id, p.lang, p.title, p.redirect.orNull))

	def prepareRevisionsTable(pages: RDD[Page]) = pages.map(p => (p.id, p.lang, p.revisions))
		.flatMap {
			case (id, lang, revisions) =>
				revisions.map {
					r =>
						(r.id, r.parentId, r.time, r.comment, r.model, r.format, r.text, r.sha1)
				}
		}

	def prepareContributorsTable(pages: RDD[Page]) =
		pages.flatMap(p => p.revisions.map(r => (r.id, r.contributor)))
			.collect {
				case (revId, cu: ContributorUser) => (revId, cu.name)
			}
}