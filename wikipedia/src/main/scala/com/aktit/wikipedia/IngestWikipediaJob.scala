package com.aktit.wikipedia

import com.aktit.wikipedia.dto._
import com.aktit.xml.XmlPartialStreaming

/**
  * convert the xml files into Page
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
			val xml = new XmlPartialStreaming
			val in = xmlIn.open()
			xml.parse(in, "page").map {
				pageXml =>
					try {
						val revisionXml = pageXml \ "revision"
						Page(
							id = (pageXml \ "id").text.trim.toLong,
							title = (pageXml \ "title").text.trim,
							lang = lang,
							redirect = (pageXml \ "redirect" \ "@title").headOption.map(_.text),
							revisions = Seq(
								Revision(
									(revisionXml \ "id").text.trim.toLong,
									extractParentId(revisionXml),
									DateTime.parse((revisionXml \ "timestamp").text.trim),
									extractContributor(revisionXml),
									(revisionXml \ "comment").text.trim,
									(revisionXml \ "model").text.trim,
									(revisionXml \ "format").text.trim,
									(revisionXml \ "text").text.trim,
									(revisionXml \ "sha1").text.trim
								)
							)
						)
					} catch {
						case e: Throwable =>
							throw new RuntimeException(s"couldn't parse xml : $pageXml", e)
					}
			}
	}

	def extractParentId(revisionXml: NodeSeq): Long = (revisionXml \ "parentid").text.trim match {
		case "" => -1l
		case x => x.toLong
	}

	def extractContributor(revisionXml: NodeSeq): Contributor = {
		val contributorXml = revisionXml \ "contributor"

		if (contributorXml.flatMap(_.child).isEmpty) {
			ContributorUnknown
		} else {
			(contributorXml \ "ip").isEmpty match {
				case true =>
					ContributorUser((contributorXml \ "id").text.trim.toLong, (contributorXml \ "username").text)
				case false =>
					ContributorIP((contributorXml \ "ip").text.trim)
			}
		}
	}

	def mergeByIdPerLang(pages: RDD[Page]): RDD[Page] = pages.keyBy(p => s"${p.id}-${p.lang}").reduceByKey({
		(p1, p2) =>
			p1.merge(p2)
	}, 128).map(_._2)
}