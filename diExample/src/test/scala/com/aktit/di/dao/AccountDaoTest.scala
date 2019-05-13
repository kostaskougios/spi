package com.aktit.di.dao

import com.aktit.di.AbstractDiSuite
import com.aktit.di.DiBuilders.account

/**
  * @author kostas.kougios
  *         13/05/19 - 17:52
  */
class AccountDaoTest extends AbstractDiSuite
{

	import session.implicits._

	test("append/read") {
		new App
		{
			val a1 = account()
			dao.append(Seq(a1).toDS)
			dao.read.toSeq should be(Seq(a1))
		}
	}

	class App
	{
		val app = createDiApp
		val dao = app.instance[AccountDao]
	}

}
