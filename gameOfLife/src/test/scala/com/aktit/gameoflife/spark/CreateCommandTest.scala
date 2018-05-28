package com.aktit.gameoflife.spark

import java.util.UUID

import com.aktit.gameoflife.model.{Edges, Sector}
import com.aktit.spark.testing.BaseSparkSuite

/**
  * @author kostas.kougios
  *         28/05/18 - 18:40
  */
class CreateCommandTest extends BaseSparkSuite
{
	test("creates sectors") {
		val (sectors, _) = createTestGame
		sectors.map(_.posX).toSet should be(Set(0, 1))
		sectors.map(_.posY).toSet should be(Set(0, 1, 2))
	}

	test("creates edges") {
		val (_, edges) = createTestGame
		edges.map(_.topSide.posX).toSet should be(Set(0, 1))
		edges.map(_.topSide.posY).toSet should be(Set(0, 1, 2))
	}

	def randomDir = s"/tmp/${UUID.randomUUID}"

	def createTestGame = {
		val out = randomDir
		val cmd = new CreateCommand("TestGame", sectorWidth = 10, sectorHeight = 5, numSectorsHorizontal = 2, numSectorsVertical = 3, howManyLiveCells = 20)
		cmd.run(sc, out)
		(
			sc.objectFile[((Int, Int), Sector)](out + "/TestGame/turn-1").collect().map(_._2).toList,
			sc.objectFile[Edges](out + "/TestGame/turn-1-edges").collect().toList
		)
	}
}
