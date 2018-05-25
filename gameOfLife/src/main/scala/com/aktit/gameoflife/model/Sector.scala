package com.aktit.gameoflife.model

/**
  * @author kostas.kougios
  *         25/05/18 - 20:26
  */
trait Sector
{
	def startX: Long

	def startY: Long

	def endX: Long

	def endY: Long

	def isLive(x: Int, y: Int): Boolean
}

object Sector
{

	private case class DenseSector(startX: Long, startY: Long, endX: Long, endY: Long)
	{
	}

}
