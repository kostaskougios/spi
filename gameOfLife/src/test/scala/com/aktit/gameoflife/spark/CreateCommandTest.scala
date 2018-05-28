package com.aktit.gameoflife.spark

import java.util.UUID

import com.aktit.gameoflife.model.Sector
import com.aktit.spark.testing.BaseSparkSuite

/**
  * @author kostas.kougios
  *         28/05/18 - 18:40
  */
class CreateCommandTest extends BaseSparkSuite
{
	test("creates sectors") {
		val out = createTestGame
		val sectors = sc.objectFile[Sector](out).collect()
		sectors.map(_.posX).toSet should be(Set(0, 1))
		sectors.map(_.posY).toSet should be(Set(0, 1, 2))
	}

	def randomDir = s"/tmp/${UUID.randomUUID}"

	def createTestGame: String = {
		val out = randomDir
		val cmd = new CreateCommand("TestGame", sectorWidth = 10, sectorHeight = 5, numSectorsHorizontal = 2, numSectorsVertical = 3, howManyLiveCells = 20)
		cmd.run(sc, out)
		out + "/TestGame/turn-1"
	}
}
