package com.aktit.wikipedia.dto

import org.joda.time.DateTime

/**
  * @author kostas.kougios
  *         17/05/18 - 15:53
  */
object WikipediaBuilder
{
	val Time = DateTime.now

	def revision(
		id: Long = 10,
		parentId: Long = 1,
		time: DateTime = Time,
		contributor: Contributor = contributorUser(),
		comment: String = "the comment",
		model: String = "the model",
		format: String = "the format",
		text: String = "the text",
		sha1: String = "the sha1"
	) = Revision(
		id,
		parentId,
		time,
		contributor,
		comment,
		model,
		format,
		text,
		sha1
	)

	def contributorUser(id: Long = 1, name: String = "kostas") = ContributorUser(id, name)
}
