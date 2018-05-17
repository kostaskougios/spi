package com.aktit.wikipedia.dto

import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         17/05/18 - 15:53
  */
class RevisionTest extends FunSuite
{
	test("breakToWords") {
		WikipediaBuilder.revision(text = "sofia, [malgré] tout aimait: :la laitue et le choux! dot.com @ $ % ^ & * ( ) - + =").breakToWords should be(Seq(
			"sofia", "malgré", "tout", "aimait", "la", "laitue", "et", "le", "choux", "dot", "com"
		))
	}

}
