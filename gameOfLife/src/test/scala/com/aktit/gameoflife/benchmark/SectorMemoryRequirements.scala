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
	val Width = 100000
	val Height = 100000
	val Live = Width * Height / 10

	val sector = {
		println(s"Creating Sector with ${(Width * Height) / 1000000} million cells and ~$Live live cells")
		val matrix = Matrix.newBuilder(Width, Height).addRandomLiveCells(Live).result()
		Sector(matrix, Boundaries.empty(Width, Height))
	}

	val sz = ObjectSizeCalculator.getObjectSize(sector)
	val serializedSz = SerializationUtils.serialize(sector).length
	println(s"Sector ready, byte size is ${sz / (1024 * 1024)} mb, serialized size is ${serializedSz / (1024 * 1024)} mb")
}
