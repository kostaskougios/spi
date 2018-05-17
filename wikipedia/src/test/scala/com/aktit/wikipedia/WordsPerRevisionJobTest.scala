package com.aktit.wikipedia

import com.aktit.spark.testing.BaseSparkSuite

/**
  * @author kostas.kougios
  *         Date: 25/09/15
  */
class WordsPerRevisionJobTest extends BaseSparkSuite
{

	import Data._
	import WordsPerRevisionJob._

	test("words") {
		val all = words(
			sc.parallelize(
				Seq(
					Page1a.copy(revisions = Seq(Rev100.copy(text = "hello once.Hello twice"))),
					Page2.copy(revisions = Seq(Rev101.copy(text = "hello again.Again and again"))))
			)
		).collect().toSeq

		all should be(Seq(
			(100, "hello"),
			(100, "once"),
			(100, "hello"),
			(100, "twice"),
			(101, "hello"),
			(101, "again"),
			(101, "again"),
			(101, "and"),
			(101, "again")
		))
	}
}
