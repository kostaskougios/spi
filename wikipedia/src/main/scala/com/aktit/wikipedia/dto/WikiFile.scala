package com.aktit.wikipedia.dto

import java.io.File

import org.apache.commons.io.FileUtils

/**
  * @author kostas.kougios
  *         06/05/18 - 22:43
  */
case class WikiFile(
	name: String,
	content: String
)

object WikiFile
{
	def fromFile(file: File) = WikiFile(
		file.getName,
		FileUtils.readFileToString(file, "UTF-8")
	)
}