package com.aktit.testconfig

import java.io.File

/**
  * @author kostas.kougios
  */
object Config
{
	val Zookeeper = "nn.lan"
	val PhoenixJdbcUrl = "jdbc:phoenix:" + Zookeeper

	def locateRootProjectFolder = {
		if (new File("build.sbt").exists) {
			new File(".").getAbsolutePath
		} else if (new File("../build.sbt").exists) {
			new File("..").getAbsolutePath
		} else throw new IllegalStateException("can't trace root project folder")
	}

}
