package com.aktit.wikipedia.explore

import com.aktit.wikipedia.dto.Page

/**
  * @author kostas.kougios
  *         Date: 25/09/15
  */
object AreRevisionIdsUnique
{
	def main(args: Array[String]): Unit = {

		val conf = new SparkConf().setAppName(getClass.getName)
		val src = "hdfs://nn.lan/wikipedia/serialized"
		val sc = new SparkContext(conf)

		try {
			val pages = sc.objectFile[Page](src)
			val revs = pages.flatMap(_.revisions).map(_.id).cache()
			val cnt = revs.count()
			val distCnt = revs.distinct().count()
			println(cnt)
			println(distCnt)
		} finally {
			sc.stop()
		}
	}

}
