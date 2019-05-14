package com.aktit.di.model

import com.aktit.di.DiBuilders.{account, timestamp, transfer}
import org.scalatest.FunSuite
import org.scalatest.Matchers._

/**
  * Testing the business logic of Account class. Note no SparkSession is involved, making testing
  * very easy.
  *
  * @author kostas.kougios
  *         14/05/19 - 08:10
  */
class AccountTest extends FunSuite
{
	test("transfer in") {
		val transferTime = timestamp(2011, 10, 1, 1, 5)
		val a = account(name = "acc1", amount = 1, lastUpdated = timestamp(2010, 5, 10, 8, 0))
			.transfer(
				Seq(transfer(accountName = "acc1", changeAmount = 5)),
				transferTime
			)

		a.amount should be(1 + 5)
		a.lastUpdated should be(transferTime)
	}

	test("transfer out") {
		val transferTime = timestamp(2011, 10, 1, 1, 5)
		val a = account(name = "acc1", amount = 1, lastUpdated = timestamp(2010, 5, 10, 8, 0))
			.transfer(
				Seq(transfer(accountName = "acc1", changeAmount = -5)),
				transferTime
			)

		a.amount should be(1 - 5)
	}

}
