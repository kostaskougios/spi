package com.aktit.wikipedia

import com.aktit.loaders.dto.XmlRow
import com.aktit.wikipedia.dto._
import org.apache.spark.SparkContext
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD

/**
  * Convert the xml files into Page.
  *
  * Run it on production via bin/wikipedia-ingest-job.
  *
  * Locally run it with say
  * -Dspark.src=hdfs://server.lan/wikipedia/src
  * -Dspark.out=/tmp/wikipedia/serialized2
  * -Dspark.master=local[4]
  *
  * @author kostas.kougios
  */
object IngestWikipediaJob extends Logging
{
	def main(args: Array[String]): Unit = {

		val conf = wikipediaSparkConf
			.setAppName(getClass.getName)

		val src = conf.get("spark.src")
		val out = conf.get("spark.out")

		val sc = new SparkContext(conf)

		try {
			val rdd = sc.objectFile[XmlRow](src)
			val data = extractDataFromXml(rdd)
			val merged = mergeByIdPerLang(data)
			merged.saveAsObjectFile(out)
		} finally {
			sc.stop()
		}
	}

	def extractDataFromXml(rdd: RDD[XmlRow]) = rdd.map {
		xmlRow =>
			val lang = xmlRow.fileName.substring(0, 2)
			Page.fromXml(xmlRow.xml, lang)
	}

	def mergeByIdPerLang(pages: RDD[Page]) =
		pages.keyBy(p => s"${p.id}-${p.lang}")
			.reduceByKey(_.merge(_), 512)
			.map(_._2)
}