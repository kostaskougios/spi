package com.aktit.di.service

import java.sql.Timestamp

import com.aktit.di.dao.{AccountDao, TransferDao}
import com.aktit.di.model.{Account, Transfer}
import javax.inject.{Inject, Singleton}
import org.apache.spark.sql.{Dataset, SparkSession}

/**
  * @author kostas.kougios
  *         13/05/19 - 17:55
  */
@Singleton
class AccountService @Inject()(session: SparkSession, accountDao: AccountDao, transferDao: TransferDao)
{

	import session.implicits._

	/**
	  * Will execute all transfers to the accounts and change the last updated time of those accounts.
	  *
	  * @param time the time to set for the changed accounts
	  */
	def executeTransfers(time: Timestamp): Unit = {
		val accounts = accountDao.read
		val transfers = transferDao.read
		val resultedAccounts = executeTransfers(time, transfers, accounts)
		accountDao.append(resultedAccounts)
	}

	/**
	  * Splitting down executeTransfers to two methods helps with testing. Here we only have to pass datasets which are
	  * easy to create.
	  */
	private[service] def executeTransfers(time: Timestamp, transfers: Dataset[Transfer], accounts: Dataset[Account]): Dataset[Account] =
		accounts.as("l").joinWith(transfers.as("r"), $"l.name" === $"r.accountName")
			.groupByKey {
				case (account, transfer) => account.name
			}.mapGroups {
			(accountName, transfersPerAccount) =>
				// Note: this runs in an executor. We can't call methods from AccountService here.
				// This is because say a call to myMethod() is actually a call to
				// this.myMethod() and spark would have to serialize AccountService in order
				// for it to be able to access it like that.
				// DI runs on the driver and the beans are not available on executors.
				val a = transfersPerAccount.toArray
				val account = a.head._1
				val transfers = a.map(_._2)
				account.transfer(transfers, time)
		}
}
