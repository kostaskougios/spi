package com.aktit.wikipedia

import com.aktit.wikipedia.dto.Page
import org.apache.phoenix.spark._
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
/**
  * Inserts word counts into hbase/phoenix database.
  *
  * Run ddl.sql against hbase before running this job.
  *
  * @author kostas.kougios
  */
object WordsPerRevisionJob extends Logging
{

	def main(args: Array[String]): Unit = {

		val conf = wikipediaSparkConf.setAppName(getClass.getName)
		val src = conf.get("spark.src")
		val zookeeper = conf.get("spark.hbase.zookeeper")

		val sc = new SparkContext(conf)

		try {
			val pages = sc.objectFile[Page](src)
			val wordsPerRevision = words(pages)
			wordsPerRevision.zipWithIndex().map {
				case ((revId, word), id) =>
					(id.toInt, revId, word)
			}.saveToPhoenix(
				"words",
				Seq("ID", "REVISION_ID", "WORD"),
				zkUrl = Some(zookeeper)
			)
		} finally {
			sc.stop()
		}
	}

	def words(pages: RDD[Page]) = pages.flatMap(_.revisions).flatMap {
		rev =>
			rev.breakToWords.map {
				word =>
					(rev.id, word.toLowerCase)
			}
	}
}