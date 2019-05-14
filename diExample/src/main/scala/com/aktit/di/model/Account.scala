package com.aktit.di.model

import java.sql.Timestamp

/**
  * An account having an amount of money
  *
  * @author kostas.kougios
  *         13/05/19 - 11:12
  */
case class Account(name: String, amount: BigDecimal, lastUpdated: Timestamp)
{
	def transfer(transfers: Seq[Transfer], time: Timestamp) = {
		// fail fast if the transfers are not for this account
		for (t <- transfers if t.accountName != name) throw new IllegalArgumentException(s"transfer() for account $name called for a transfer $t")

		// do the actual transfer
		Account(
			name,
			amount + transfers.map(_.changeAmount).sum,
			time
		)
	}

}