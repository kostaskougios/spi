package com.aktit.di.model

import java.sql.Timestamp

/**
  * An account having an amount of money
  *
  * @author kostas.kougios
  *         13/05/19 - 11:12
  */
case class Account(name: String, amount: BigDecimal, lastUpdated: Timestamp)