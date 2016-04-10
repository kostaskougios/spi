package hbase

import com.google.common.primitives.Ints
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put

/**
  * run
  *
  * hbase shell
  *
  * and then
  *
  * create 'test', 'cf'
  *
  * @author kostas.kougios
  *         Date: 06/04/16
  */
object PutDataIntoTable extends App
{
	withConnection { connection =>
		val table = connection.getTable(TableName.valueOf("test"))

		val put = new Put("put-data-sample".getBytes)
		// put an int into cf:sample-int
		put.addColumn("cf".getBytes, "sample-int".getBytes, Ints.toByteArray(100))
		table.put(put)
	}
}
