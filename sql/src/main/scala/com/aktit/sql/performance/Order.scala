package com.aktit.sql.performance

import java.sql.Timestamp

/**
  * Denormalized order to help avoid joins
  *
  * @author kostas.kougios
  *         11/07/18 - 13:31
  */
case class Order(
	userId: Long,
	orderNo: String,
	date: Timestamp,
	productId: Int,
	productCode: String,
	productTitle: String,
	productPrice: Float,
	boughtPrice: Float,
	discountPercentageApplied: Byte
)