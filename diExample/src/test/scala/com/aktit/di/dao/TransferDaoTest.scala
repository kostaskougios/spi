package com.aktit.di.dao

import com.aktit.di.AbstractDiSuite
import com.aktit.di.DiBuilders.transfer

/**
  * @author kostas.kougios
  *         13/05/19 - 11:24
  */
class TransferDaoTest extends AbstractDiSuite
{

	import session.implicits._

	test("append/read") {
		new App
		{
			private val t1 = transfer()
			dao.append(Seq(t1).toDS)
			dao.read.toSeq should be(Seq(t1))
		}
	}

	class App
	{
		val app = createDiApp
		val dao = app.instance[TransferDao]
	}

}
