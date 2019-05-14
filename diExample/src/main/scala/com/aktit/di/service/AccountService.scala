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

	def executeTransfers(time: Timestamp): Unit = {
		val accounts = accountDao.read
		val transfers = transferDao.read
		val resultedAccounts = executeTransfers(time, transfers, accounts)
		accountDao.append(resultedAccounts)
	}

	def executeTransfers(time: Timestamp, transfers: Dataset[Transfer], accounts: Dataset[Account]): Dataset[Account] =
		accounts.as("l").joinWith(transfers.as("r"), $"l.name" === $"r.accountName")
			.groupByKey {
				case (account, transfer) => account.name
			}.mapGroups {
			(accountName, transfersPerAccount) =>
				val a = transfersPerAccount.toArray
				val account = a.head._1
				val transfers = a.map(_._2)
				account.transfer(transfers, time)
		}
}
