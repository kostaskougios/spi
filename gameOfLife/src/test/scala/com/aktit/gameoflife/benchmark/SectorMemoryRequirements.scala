package com.aktit.gameoflife.benchmark

import com.aktit.gameoflife.model.{Boundaries, Matrix, Sector}
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
import org.apache.commons.lang3.SerializationUtils
import org.apache.spark.util.SizeEstimator

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
		Sector(0, 0, matrix, Boundaries.empty(Width, Height))
	}

	val sz = ObjectSizeCalculator.getObjectSize(sector)
	val serializedSz = SerializationUtils.serialize(sector).length
	val sparkSz = SizeEstimator.estimate(sector)
	println(s"Sector ready, byte size is ${toMB(sz)} mb, serialized size is ${toMB(serializedSz)} mb, spark will size it at ${toMB(sparkSz)} mb")

	def toMB(sz: Long) = sz / (1024 * 1024)
}
