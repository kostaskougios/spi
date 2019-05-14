# Dependency injection example

This is an example of how dependency injection can be applied on spark applications to
allow a layered approach to pipelines, simplify component creation, wiring and testing.

A lot of spark applications start of as a number of objects. This is due to the way spark
runs code in the driver and executor and to avoid spark serializing components. But having
all components as objects is hard to manage and to test.

Here we are going to have a look at using dependency injection (DI) to create components for
code that executes in the spark driver. And we will see how we can actually use the domain
model to do the business logic and how to use and test that independently of spark.

The idea is to create components like dao's and services and wire them using DI. Those
components are - like a non-spark app - responsible for loading data and delegating
business logic to our domain model.

The example is about transfering money from an account.The entities in this example are
the Account and Transfer class.

```scala
case class Account(name: String, amount: BigDecimal, lastUpdated: Timestamp) {
	// ... business logic ...
}

case class Transfer(accountName: String, changeAmount: BigDecimal)
```

We can then have the dao's which will be writing/reading the data into orc files. To
simplify the dao's, we have an `AbstractDao` class which contains most of the implementation.

```scala
abstract class AbstractDao[A <: Product : TypeTag](spark: SparkSession, path: String)
{
	private implicit val encoder = Encoders.product[A]

	def read: Dataset[A] = spark.read.orc(path).as[A]

	def append(ds: Dataset[A]): Unit = ds.write.mode(SaveMode.Append).orc(path)
}
``` 

Notice that this class requires SparkSession to be provided. But we can use DI to do that. So
for example `AccountDao` looks like this: 

```scala
@Singleton
class AccountDao @Inject()(spark: SparkSession, appConfig: AppConfig)
	extends AbstractDao[Account](spark, appConfig.rootPath + "/account")
```

Using constructor injections, we will inject both the `SparkSession` and a configuration
class `AppConfig` that we use to specify where data are stored. 

The final component is the service:

```scala
@Singleton
class AccountService @Inject()(session: SparkSession, accountDao: AccountDao, transferDao: TransferDao) {
	// ... get data from both dao's, join them and run the business logic using the domain model...
}
```

Here again we provided the dao's and spark session via constructor injection.

Now lets have a look at all the code in `AccountService`. There are 2 interesting things
here (please ignore the join code which just brings the data together). 1st is that we
can use the dao's and 2nd when we load all the data we delegate business logic to the
domain class :

```scala
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
```

`executeTransfers(time: Timestamp)` uses the dao's to read and write the data. Internally it
joins the accounts by their name and applies all the transfers per account. When all the data
have been joined, the actual business logic of transfering money is delegated to the domain
model. Using the domain model is a good practise and also we don't need an extra method in
the service layer.

Here is the full `Account` implementation:

```scala
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
``` 

It doesn't depend on spark and can be easily tested.

```scala
import com.aktit.di.DiBuilders.{account, timestamp, transfer}

class AccountTest extends FunSuite
{
	val transferTime = timestamp(2011, 10, 1, 1, 5)

	test("transfer time is set") {
		val a = account(name = "acc1", amount = 1, lastUpdated = timestamp(2010, 5, 10, 8, 0))
			.transfer(Seq(transfer(accountName = "acc1", changeAmount = 5)), transferTime)

		a.lastUpdated should be(transferTime)
	}
// .... more tests
}

```

Now the `AccountService` test has to use a SparkSession to be tested but the tests have to
only test if the data are fetched and joined correctly. The actual money transfer logic is
delegated to the domain class and doesn't have to be retested:

```scala
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
// ... more tests
}

```