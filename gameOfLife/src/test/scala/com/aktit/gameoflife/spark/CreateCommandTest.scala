package com.aktit.gameoflife.spark

import java.util.UUID

import com.aktit.gameoflife.model.{Edges, Sector, Universe}
import com.aktit.gameoflife.spark.Directories._
import com.aktit.spark.testing.BaseSparkSuite

/**
  * @author kostas.kougios
  *         28/05/18 - 18:40
  */
class CreateCommandTest extends BaseSparkSuite
{
	test("universe") {
		val (universe, _, _) = createTestGame
		universe should be(Universe("TestGame", 2, 3, 10, 5))
	}

	test("creates sectors") {
		val (_, sectors, _) = createTestGame
		sectors.map(_.posX).toSet should be(Set(0, 1))
		sectors.map(_.posY).toSet should be(Set(0, 1, 2))
	}

	test("creates edges") {
		val (_, _, edges) = createTestGame
		edges.map(_.posX).toSet should be(Set(0, 1))
		edges.map(_.posY).toSet should be(Set(0, 1, 2))
	}

	def randomDir = s"/tmp/${UUID.randomUUID}"

	def createTestGame = {
		val out = randomDir
		val cmd = new CreateCommand("TestGame", sectorWidth = 10, sectorHeight = 5, numSectorsHorizontal = 2, numSectorsVertical = 3, howManyLiveCellsPerSector = 20)
		cmd.run(sc, out)
		(
			sc.objectFile[Universe](turnUniverseDir(out, "TestGame", 1)).collect().head,
			sc.objectFile[Sector](turnSectorDir(out, "TestGame", 1)).collect().toList,
			sc.objectFile[Edges](turnEdgesDir(out, "TestGame", 1)).collect().toList
		)
	}
}
