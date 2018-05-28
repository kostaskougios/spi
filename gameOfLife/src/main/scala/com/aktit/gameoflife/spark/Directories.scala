package com.aktit.gameoflife.spark

/**
  * @author kostas.kougios
  *         28/05/18 - 22:16
  */
object Directories
{
	def turnUniverseDir(out: String, gameName: String, turn: Int) = out + "/" + gameName + s"/turn-" + turn + "/universe"

	def turnSectorDir(out: String, gameName: String, turn: Int) = out + "/" + gameName + s"/turn-" + turn + "/sectors"

	def turnEdgesDir(out: String, gameName: String, turn: Int) = out + "/" + gameName + s"/turn-" + turn + "/edges"

}
