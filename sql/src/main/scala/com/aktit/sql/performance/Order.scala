package com.aktit.sql.performance

import java.sql.Timestamp

/**
  * Denormalized order to help avoid joins.
  *
  * Note: fields are lower case so that they are compatible with hive
  *
  * @author kostas.kougios
  *         11/07/18 - 13:31
  */
case class Order(
	userid: Long,
	orderno: String,
	date: Timestamp,
	productid: Int,
	productcode: String,
	producttitle: String,
	productprice: Float,
	boughtprice: Float,
	discountpercentageapplied: Byte
)