package com.aktit.wikipedia.dto

import org.joda.time.DateTime

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
case class Revision(
	id: Long,
	parentId: Long,
	time: DateTime,
	contributor: Contributor,
	comment: String,
	model: String,
	format: String,
	text: String,
	sha1: String
)