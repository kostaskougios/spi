package com.aktit.gameoflife.benchmark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
import org.apache.commons.lang3.SerializationUtils

/**
  * Memory requirements of a fully live sector
  *
  * @author kostas.kougios
  */
object SectorMemoryRequirements extends App
{
	val Width = 8000
	val Height = 8000

	val sector = {
		println(s"Creating ${(Width * Height) / 1000000} million live nodes")
		val matrix = Matrix(
			Width,
			Height,
			for {
				x <- 0 until Width
				y <- 0 until Height
			} yield (x, y))
		Sector(matrix, Boundaries.empty(Width, Height))
	}

	val sz = ObjectSizeCalculator.getObjectSize(sector)
	val serializedSz = SerializationUtils.serialize(sector).length
	println(s"Sector ready, byte size is ${sz / (1024 * 1024)} mb, serialized size is ${serializedSz / (1024 * 1024)} mb, please profile now")

	Thread.sleep(86400 * 1000)

}
