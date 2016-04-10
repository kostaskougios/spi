

/**
  * @author kostas.kougios
  *         Date: 06/04/16
  */
package object hbase
{
	def withConnection(f: Connection => Unit): Unit = {
		val hConf: Configuration = hbaseConfig
		val connection = ConnectionFactory.createConnection(hConf)
		try {
			f(connection)
		} finally {
			connection.close()
		}
	}

	def hbaseConfig: Configuration = {
		val hConf = HBaseConfiguration.create
		hConf.set("hbase.zookeeper.quorum", "d1.lan,d2.lan,d3.lan")
		hConf
	}
}
