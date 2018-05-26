package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.sector
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         26/05/18 - 19:08
  */
class SectorTest extends FunSuite
{
	test("isLive") {
		val s = sector(liveCoordinates = Seq((2, 3)))
		s.isLive(2, 3) should be(true)
	}
}
