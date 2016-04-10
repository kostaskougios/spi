package com.aktit.wikipedia

import com.aktit.wikipedia.dto.{ContributorUser, Page, Revision}

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
object Data
{
	val Rev100 = Revision(100, 10, DateTime.now, ContributorUser(5, "kostas"), "comment1", "model", "format", "text1", "sha1")
	val Rev101 = Revision(101, 100, DateTime.now, ContributorUser(6, "nick"), "comment2", "model", "format", "text2", "sha2")

	val Page1a = Page(1, "page1", "en", Some("redirect1"), Seq(Rev100))
	val Page1b = Page(1, "page1", "en", Some("redirect1"), Seq(Rev101))
	val Page2 = Page(2, "page2", "en", None, Seq(Revision(102, 11, DateTime.now, ContributorUser(6, "nick"), "comment3", "model", "format", "text]3", "sha3")))

}
