package com.aktit.di

import java.sql.Timestamp
import java.time.LocalDateTime

import com.aktit.di.model.{Account, Transfer}

/**
  * @author kostas.kougios
  *         13/05/19 - 11:29
  */
object DiBuilders
{
	def timestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int) = Timestamp.valueOf(LocalDateTime.of(year, month, day, hour, minute))

	def transfer(
		accountName: String = "accountName",
		changeAmount: BigDecimal = 5.5
	) = Transfer(accountName, changeAmount)

	def account(
		name: String = "name",
		amount: BigDecimal = 6.6,
		lastUpdated: Timestamp = timestamp(2010, 12, 21, 20, 18)
	) = Account(name, amount, lastUpdated)
}
