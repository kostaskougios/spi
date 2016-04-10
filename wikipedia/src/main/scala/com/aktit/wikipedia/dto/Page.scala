package com.aktit.wikipedia.dto

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
		if (title != p2.title || lang != p2.lang || id != p2.id) throw new IllegalArgumentException(s"pages have same id but different title : ${id} / ${lang} / ${title} - ${p2.id} / ${p2.lang} / ${p2.title}")
		copy(revisions = revisions ++ p2.revisions)
	}
}
