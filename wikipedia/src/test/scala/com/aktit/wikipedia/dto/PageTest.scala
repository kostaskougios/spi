package com.aktit.wikipedia.dto

import com.aktit.wikipedia.Data
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios Date: 23/09/15
  */
class PageTest extends AnyFunSuite {

  import Data._

  test("merge") {
    Page1a.merge(Page1b) should be(Page1a.copy(revisions = Page1a.revisions ++ Page1b.revisions))
  }
}
