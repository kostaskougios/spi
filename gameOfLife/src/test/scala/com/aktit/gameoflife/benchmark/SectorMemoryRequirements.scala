package com.aktit.gameoflife.benchmark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
import org.apache.commons.lang3.SerializationUtils

/**
  * Memory requirements of a fully live sector and also for creating one.
  *
  * @author kostas.kougios
  */
object SectorMemoryRequirements extends App
{
	val Width = 80000
	val Height = 80000

	val width64 = Width / 64
	val sector = {
		println(s"Creating ${(Width * Height) / 1000000} million live nodes")
		val matrix = Matrix.fastRandom(width64, Height)
		Sector(matrix, Boundaries.empty(width64 * 64, Height))
	}

	val sz = ObjectSizeCalculator.getObjectSize(sector)
	val serializedSz = SerializationUtils.serialize(sector).length
	println(s"Sector ready, byte size is ${sz / (1024 * 1024)} mb, serialized size is ${serializedSz / (1024 * 1024)} mb, please profile now")

	Thread.sleep(86400 * 1000)

}
