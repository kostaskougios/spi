package com.aktit.wikipedia.dto

import com.aktit.dto.EpochDateTime
import org.joda.time.DateTime

import scala.xml.{Node, NodeSeq}

/**
  * a wikipedia page
  *
  * @author kostas.kougios
  *         Date: 23/09/15
  */
case class Page(
	id: Long,
	title: String,
	lang: String,
	redirect: Option[String],
	revisions: Seq[Revision]
)
{
	def merge(p2: Page) = {
		if (title != p2.title || lang != p2.lang || id != p2.id) throw new IllegalArgumentException(s"pages have same id but different title : $id / $lang / $title - ${p2.id} / ${p2.lang} / ${p2.title}")
		copy(revisions = revisions ++ p2.revisions)
	}
}

object Page
{
	def fromXml(pageXml: Node, lang: String) = {
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
						EpochDateTime(DateTime.parse((revisionXml \ "timestamp").text.trim)),
						Contributor.fromXml(revisionXml),
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

	private def extractParentId(revisionXml: NodeSeq): Long = (revisionXml \ "parentid").text.trim match {
		case "" => -1l
		case x => x.toLong
	}

}