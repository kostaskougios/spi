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

	test("liveNeighbours, no neighbours") {
		sector().liveNeighbours(2, 3) should be(0)
	}

	test("liveNeighbours, vertical neighbours are counted") {
		sector(liveCoordinates = Seq((2, 2), (2, 4))).liveNeighbours(2, 3) should be(2)
	}

	test("liveNeighbours, horizontal neighbours are counted") {
		sector(liveCoordinates = Seq((2, 2), (4, 2))).liveNeighbours(3, 2) should be(2)
	}

	test("liveNeighbours, diagonal neighbours are counted") {
		sector(liveCoordinates = Seq((2, 2), (4, 2), (2, 4), (4, 4))).liveNeighbours(3, 3) should be(4)
	}

	test("liveNeighbours, top left boundary neighbours are counted") {
		sector(boundaries = boundaries(top = Array(-1, 0, 1), left = Array(0, 1))).liveNeighbours(0, 0) should be(5)
	}

	test("liveNeighbours, bottom left boundary neighbours are counted") {
		sector(boundaries = boundaries(bottom = Array(-1, 0, 1), left = Array(3, 4))).liveNeighbours(0, 4) should be(5)
	}

	test("liveNeighbours, top right boundary neighbours are counted") {
		sector(boundaries = boundaries(top = Array(8, 9, 10), right = Array(0, 1))).liveNeighbours(9, 0) should be(5)
	}

	test("liveNeighbours, bottom right boundary neighbours are counted") {
		sector(boundaries = boundaries(bottom = Array(8, 9, 10), right = Array(3, 4))).liveNeighbours(9, 4) should be(5)
	}

}
