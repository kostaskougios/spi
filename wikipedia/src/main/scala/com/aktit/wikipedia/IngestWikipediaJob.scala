package com.aktit.wikipedia

import com.aktit.wikipedia.dto._
import com.aktit.xml.XmlPartialStreaming
import org.apache.commons.lang3.StringUtils
import org.apache.spark.input.PortableDataStream
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

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

		val conf = new SparkConf().setAppName(getClass.getName)
		val src = conf.get("spark.src")
		val out = conf.get("spark.out")

		val sc = new SparkContext(conf)

		try {
			val rdd = sc.binaryFiles(src, minPartitions = 4)
			val data = extractDataFromXml(rdd)
			val merged = mergeByIdPerLang(data)
			merged.saveAsObjectFile(out)
		} finally {
			sc.stop()
		}
	}

	def extractDataFromXml(rdd: RDD[(String, PortableDataStream)]) = rdd.flatMap {
		case (file, xmlIn) =>
			logInfo(s"processing $file")
			val name = StringUtils.substringAfterLast(file, "/")
			val lang = name.substring(0, 2)
			val in = xmlIn.open()
			val xml = new XmlPartialStreaming
			xml.parse(in, "page").map(Page.fromXml(_, lang))
	}

	def mergeByIdPerLang(pages: RDD[Page]): RDD[Page] = pages.keyBy(p => s"${p.id}-${p.lang}").reduceByKey({
		(p1, p2) =>
			p1.merge(p2)
	}, 128).map(_._2)
}