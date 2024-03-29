package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.{boundaries, sector}
import org.apache.commons.lang3.SerializationUtils
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

/** @author
  *   kostas.kougios 26/05/18 - 19:08
  */
class SectorTest extends AnyFunSuite {
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

  test("evolve: Any live cell with fewer than two live neighbors dies, as if by under population.") {
    sector(liveCoordinates = Seq((1, 1), (2, 1))).evolve.isLive(1, 1) should be(false)
    sector(liveCoordinates = Seq((1, 1))).evolve.isLive(1, 1) should be(false)
  }

  test("evolve: Any live cell with two or three live neighbors lives on to the next generation.") {
    sector(liveCoordinates = Seq((1, 1), (2, 1), (0, 1))).evolve.isLive(1, 1) should be(true)
    sector(liveCoordinates = Seq((1, 1), (2, 1), (0, 1), (1, 2))).evolve.isLive(1, 1) should be(true)
  }

  test("evolve: Any live cell with more than three live neighbors dies, as if by overpopulation.") {
    sector(liveCoordinates = Seq((1, 1), (2, 1), (0, 1), (1, 2), (0, 2))).evolve.isLive(1, 1) should be(false)
  }

  test("evolve: Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.") {
    sector(liveCoordinates = Seq((1, 1), (2, 1), (0, 1))).evolve.isLive(1, 2) should be(true)
  }

  test("edges, topLeft corner positive") {
    sector(liveCoordinates = Seq((0, 0))).edges.topLeftCorner.alive should be(true)
  }

  test("edges, topLeft corner negative") {
    sector().edges.topLeftCorner.alive should be(false)
  }

  test("edges, topRight corner positive") {
    sector(liveCoordinates = Seq((9, 0))).edges.topRightCorner.alive should be(true)
  }

  test("edges, topRight corner negative") {
    sector().edges.topRightCorner.alive should be(false)
  }

  test("edges, bottomLeft corner positive") {
    sector(liveCoordinates = Seq((0, 4))).edges.bottomLeftCorner.alive should be(true)
  }

  test("edges, bottomLeft corner negative") {
    sector().edges.bottomLeftCorner.alive should be(false)
  }

  test("edges, bottomRight corner positive") {
    sector(liveCoordinates = Seq((9, 4))).edges.bottomRightCorner.alive should be(true)
  }

  test("edges, bottomRight corner negative") {
    sector().edges.bottomRightCorner.alive should be(false)
  }

  test("edges, top side") {
    val side = sector(liveCoordinates = Seq((0, 0), (2, 0))).edges.topSide
    side.isLive(0) should be(true)
    side.isLive(1) should be(false)
    side.isLive(2) should be(true)
    side.isLive(3) should be(false)
  }

  test("edges, bottom side") {
    val side = sector(liveCoordinates = Seq((0, 4), (2, 4))).edges.bottomSide
    side.isLive(0) should be(true)
    side.isLive(1) should be(false)
    side.isLive(2) should be(true)
    side.isLive(3) should be(false)
  }

  test("edges, left side") {
    val side = sector(liveCoordinates = Seq((0, 0), (0, 2))).edges.leftSide
    side.isLive(0) should be(true)
    side.isLive(1) should be(false)
    side.isLive(2) should be(true)
    side.isLive(3) should be(false)
  }

  test("edges, right side") {
    val side = sector(liveCoordinates = Seq((9, 0), (9, 2))).edges.rightSide
    side.isLive(0) should be(true)
    side.isLive(1) should be(false)
    side.isLive(2) should be(true)
    side.isLive(3) should be(false)
  }

  test("evolve: no thread sync issues") {
    for (_ <- 1 to 1000) {
      val s1 = Sector(0, 0, Matrix.newBuilder(10, 10).addRandomLiveCells(50).result(), Boundaries.empty(10, 10))
      s1.evolve should be(s1.evolve)
      s1.evolve.evolve should be(s1.evolve.evolve)
    }
  }

  test("Sector is serializable") {
    val s = sector(liveCoordinates = Seq((1, 1), (2, 1)), boundaries = boundaries(top = Array(1, 2, 3)))
    SerializationUtils.serialize(s)
  }
}
