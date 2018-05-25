package com.aktit.gameoflife.model

import com.aktit.gameoflife.model.ModelBuilders.matrix
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * @author kostas.kougios
  *         25/05/18 - 23:02
  */
class MatrixTest extends FunSuite
{
	test("isLive top-left") {
		val m = matrix(liveCoordinates = Seq((0, 0)))
		m.isLive(0, 0) should be(true)
	}

	test("isLive bottom-left") {
		val m = matrix(liveCoordinates = Seq((9, 4)))
		m.isLive(9, 4) should be(true)
	}

	test("isLive top-right") {
		val m = matrix(liveCoordinates = Seq((9, 0)))
		m.isLive(9, 0) should be(true)
	}

	test("isLive bottom-right") {
		val m = matrix(liveCoordinates = Seq((9, 4)))
		m.isLive(9, 4) should be(true)
	}

	test("isLive negative") {
		val m = matrix(liveCoordinates = Seq((1, 1)))
		m.isLive(1, 0) should be(false)
		m.isLive(0, 1) should be(false)
	}

}
