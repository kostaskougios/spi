package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.{boundaries, sector}
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         26/05/18 - 19:08
  */
class SectorTest extends FunSuite
{
	test("isLive positive") {
		sector(liveCoordinates = Seq((2, 3))).isLive(2, 3) should be(true)
	}

	test("isLive negative") {
		sector(liveCoordinates = Seq((2, 3))).isLive(3, 2) should be(false)
	}

	test("isLive left boundary negative") {
		sector().isLive(-1, 0) should be(false)
	}

	test("isLive left boundary positive") {
		sector(boundaries = boundaries(left = Array(2))).isLive(-1, 2) should be(true)
	}

	test("isLive right boundary negative") {
		sector().isLive(10, 0) should be(false)
	}

	test("isLive right boundary positive") {
		sector(boundaries = boundaries(right = Array(2))).isLive(10, 2) should be(true)
	}

	test("isLive top boundary negative") {
		sector().isLive(5, -1) should be(false)
	}

	test("isLive top boundary positive") {
		sector(boundaries = boundaries(top = Array(5))).isLive(5, -1) should be(true)
	}

	test("isLive bottom boundary negative") {
		sector().isLive(3, 5) should be(false)
	}

	test("isLive bottom boundary positive") {
		sector(boundaries = boundaries(bottom = Array(3))).isLive(3, 5) should be(true)
	}

}
