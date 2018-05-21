package com.aktit.loaders.dto

/**
  * @author kostas.kougios
  *         21/05/18 - 11:41
  */
object LoaderBuilders
{
	def textFile(
		name: String = "textfile-name",
		content: String = "textfile-content"
	) = TextFile(
		name,
		content
	)
}
