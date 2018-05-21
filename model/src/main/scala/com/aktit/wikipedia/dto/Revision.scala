package com.aktit.wikipedia.dto

import java.util.StringTokenizer

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
{
	def breakToWords = {
		val tokenizer = new StringTokenizer(text, " |/{}\t\n\r\f,.:;?![]'@$%^&*()-+=\"'")
		val b = Seq.newBuilder[String]
		while (tokenizer.hasMoreElements) {
			val n = tokenizer.nextToken()
			if (!n.isEmpty) b += n
		}
		b.result
	}

}