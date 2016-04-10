package com.aktit.wikipedia.dto

import com.aktit.wikipedia.Data

/**
  * @author kostas.kougios
  *         Date: 23/09/15
  */
class PageTest extends FunSuite
{

	import Data._

	test("merge") {
		Page1a.merge(Page1b) should be(Page1a.copy(revisions = Page1a.revisions ++ Page1b.revisions))
	}
}
