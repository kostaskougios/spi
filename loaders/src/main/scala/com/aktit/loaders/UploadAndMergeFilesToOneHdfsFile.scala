package com.aktit.loaders

import com.aktit.loaders.dto.TextFile
import org.apache.commons.io.FileUtils
import org.apache.spark.internal.Logging
import org.apache.spark.{SparkConf, SparkContext}

import java.io.File
import scala.jdk.CollectionConverters._

/** It takes a lot of time to upload small files via hdfs dfs -copyFromLocal and also to process them as single files is slower. Instead we serialize them to a
  * big single hdfs file.
  *
  * This needs to run locally on a machine with the files. Run it with i.e.:
  *
  * -Dspark.src=/home/ariskk/temp/articles -Dspark.file-extensions=xml -Dspark.out=hdfs://server.lan/wikipedia/src -Dspark.master=local[4]
  *
  * @author
  *   kostas.kougios
  */
object UploadAndMergeFilesToOneHdfsFile extends Logging {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName(getClass.getName)
    val src = conf.get("spark.src")
    val srcDir = new File(src)
    if (!srcDir.exists) throw new IllegalArgumentException(s"Source directory $src not found.")
    val fileExtensions = conf.get("spark.file-extensions").split(",")
    val outDir = conf.get("spark.out")

    val sc = new SparkContext(conf)

    try {
      val allFiles = FileUtils.listFiles(srcDir, fileExtensions, true).asScala.toList
      logInfo(s"Files : ${allFiles.size}")
      sc.parallelize(allFiles).map(TextFile.fromFile).saveAsObjectFile(outDir)
    } finally {
      sc.stop()
    }
  }

}
