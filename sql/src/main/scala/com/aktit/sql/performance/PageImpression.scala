package com.aktit.sql.performance

import java.sql.Timestamp

/**
  * @author kostas.kougios
  *         10/07/18 - 09:33
  */
case class PageImpression(
	userId: Long,
	date: Timestamp,
	refererUrl: String
)