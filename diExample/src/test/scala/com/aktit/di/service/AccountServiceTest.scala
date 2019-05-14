package com.aktit.di.service

import com.aktit.di.AbstractDiSuite
import com.aktit.di.DiBuilders.{account, timestamp, transfer}
import com.aktit.di.dao.{AccountDao, TransferDao}

/**
  * @author kostas.kougios
  *         14/05/19 - 08:17
  */
class AccountServiceTest extends AbstractDiSuite
{
	val transferTime = timestamp(2011, 10, 1, 1, 5)

	import session.implicits._

	test("transfers are executed by account name") {
		new App
		{
			service.executeTransfers(
				transferTime,
				Seq(
					transfer(accountName = "acc1", changeAmount = 5),
					transfer(accountName = "acc2", changeAmount = 10)
				).toDS,
				Seq(
					account(name = "acc1", amount = 1, lastUpdated = timestamp(2010, 5, 10, 8, 0)),
					account(name = "acc2", amount = 2, lastUpdated = timestamp(2010, 8, 11, 8, 0))
				).toDS
			).toSet should be(Set(
				account(name = "acc1", amount = 6, lastUpdated = transferTime),
				account(name = "acc2", amount = 12, lastUpdated = transferTime)
			))
		}
	}

	test("executeTransfers end to end") {
		new App
		{
			val accountDao = app.instance[AccountDao]
			val transferDao = app.instance[TransferDao]

			val acc = account(name = "acc1", amount = 1, lastUpdated = timestamp(2010, 5, 10, 8, 0))
			accountDao.append(Seq(acc).toDS)
			transferDao.append(Seq(transfer(accountName = "acc1", changeAmount = 5)).toDS)
			service.executeTransfers(transferTime)
			accountDao.read.toSet should be(Set(
				acc,
				account(name = "acc1", amount = 6, lastUpdated = transferTime)
			))
		}
	}

	class App
	{
		val app = createDiApp
		val service = app.instance[AccountService]
	}

}
