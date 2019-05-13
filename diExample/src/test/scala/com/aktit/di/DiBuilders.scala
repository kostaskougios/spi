package com.aktit.di

import com.aktit.di.model.Transfer

/**
  * @author kostas.kougios
  *         13/05/19 - 11:29
  */
object DiBuilders
{
	def transfer(
		fromAccountName: String = "fromAccountName",
		toAccountName: String = "toAccountName",
		amount: BigDecimal = 5.5
	) = Transfer(fromAccountName, toAccountName, amount)
}
