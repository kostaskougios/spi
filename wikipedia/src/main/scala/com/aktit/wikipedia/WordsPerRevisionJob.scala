package com.aktit.wikipedia

import java.util.StringTokenizer

import com.aktit.wikipedia.dto.Page
import org.apache.phoenix.spark._
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
/**
  * Inserts word counts into hbase/phoenix database
  *
  * @author kostas.kougios
  */
object WordsPerRevisionJob extends Logging
{

	def main(args: Array[String]): Unit = {

		val conf = new SparkConf().setAppName(getClass.getName)
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
			break(rev.text).map {
				word =>
					(rev.id, word.toLowerCase)
			}
	}

	def break(text: String) = {
		val tokenizer = new StringTokenizer(text, " \t\n\r\f,.:;?![]'@$%^&*()-+=\"'")
		val b = Seq.newBuilder[String]
		while (tokenizer.hasMoreElements) {
			b += tokenizer.nextToken()
		}
		b.result()
	}
}