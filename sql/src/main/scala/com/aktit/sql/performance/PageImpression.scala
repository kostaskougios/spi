package com.aktit.sql.performance

import java.sql.Timestamp

/**
  * Note: fields are lower case so that they are compatible with hive
  *
  * @author kostas.kougios
  *         10/07/18 - 09:33
  */
case class PageImpression(
	userid: Long,
	date: Timestamp,
	refererurl: String
)