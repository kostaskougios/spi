package com.aktit.loaders

import java.io.{File, FileInputStream}

import com.aktit.loaders.dto.XmlRow
import com.aktit.xml.XmlPartialStreaming
import org.apache.commons.io.FileUtils
import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

/**
  * Breaks up big xml files into small xml chunks which can be parallelized when
  * processed on hadoop.
  *
  * This needs to run locally on a machine with the files. Run it with i.e.:
  *
  * -Dspark.src=/home/ariskk/temp/wikipedia
  * -Dspark.out=hdfs://server.lan/wikipedia/src
  * -Dspark.file-extensions=xml
  * -Dspark.breakup-element=page
  * -Dspark.master=local[4]
  *
  * @author kostas.kougios
  */
object BreakupBigXmlFilesAndStoreToHdfs extends Logging
{
	def main(args: Array[String]): Unit = {
		XmlPartialStreaming.setup()

		val conf = new SparkConf().setAppName(getClass.getName)
		val src = conf.get("spark.src")
		val srcDir = new File(src)
		if (!srcDir.exists) throw new IllegalArgumentException(s"Source directory $src not found.")
		val fileExtensions = conf.get("spark.file-extensions").split(",")
		val breakupElement = conf.get("spark.breakup-element")
		val outDir = conf.get("spark.out")

		val sc = new SparkContext(conf)

		try {
			val allFiles = FileUtils.listFiles(srcDir, fileExtensions, true).asScala.toList
			logInfo(s"Files : ${allFiles.size}")
			sc.parallelize(allFiles)
				.flatMap { file =>
					logInfo(s"processing file $file")
					val xml = new XmlPartialStreaming
					xml.parse(new FileInputStream(file), breakupElement)
						.map(XmlRow(file.getName, _))
				}.saveAsObjectFile(outDir)
		} finally {
			sc.stop()
		}
	}

}