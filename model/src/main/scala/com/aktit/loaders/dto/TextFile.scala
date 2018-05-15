package com.aktit.loaders.dto

import java.io.File

import org.apache.commons.io.FileUtils

/**
  * @author kostas.kougios
  *         06/05/18 - 22:43
  */
case class TextFile(
	name: String,
	content: String
)

object TextFile
{
	def fromFile(file: File) = TextFile(
		file.getName,
		FileUtils.readFileToString(file, "UTF-8")
	)
}