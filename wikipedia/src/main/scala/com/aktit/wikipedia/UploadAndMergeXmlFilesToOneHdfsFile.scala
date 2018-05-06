package com.aktit.wikipedia

import java.io.File

import com.aktit.wikipedia.dto.WikiFile
import org.apache.commons.io.FileUtils
import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

/**
  * Wikipedia xml files are many single xml files. It takes a lot of time
  * to upload those to hadoop via hdfs dfs -copyFromLocal and also to
  * process them as single files is slower and tricky. Instead we
  * serialize them to a big single hdfs file.
  *
  * This needs to run locally on a machine with the wikipedia files. Run it with i.e.:
  *
  * -Dspark.src=/home/ariskk/temp/articles -Dspark.out=hdfs://server.lan/wikipedia/src -Dspark.master=local[4]
  *
  * @author kostas.kougios
  */
object UploadAndMergeXmlFilesToOneHdfsFile extends Logging
{

	def main(args: Array[String]): Unit = {

		val conf = new SparkConf().setAppName(getClass.getName)
		val src = conf.get("spark.src")
		val srcDir = new File(src)
		if (!srcDir.exists) throw new IllegalArgumentException(s"Source directory $src not found.")
		val outDir = conf.get("spark.out")

		val sc = new SparkContext(conf)

		try {
			val allFiles = FileUtils.listFiles(srcDir, Array("nxml"), true).asScala.toList
			logInfo(s"Files : ${allFiles.size}")
			sc.parallelize(allFiles).map(WikiFile.fromFile).saveAsObjectFile(outDir)
		} finally {
			sc.stop()
		}
	}

}