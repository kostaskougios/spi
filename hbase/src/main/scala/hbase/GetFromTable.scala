package hbase

import com.google.common.primitives.Ints
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Get

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
object GetFromTable extends App
{
	withConnection { connection =>
		val table = connection.getTable(TableName.valueOf("test"))

		val g = new Get("put-data-sample".getBytes)
		val r = table.get(g)
		println(r)
		val v = r.getValue("cf".getBytes, "sample-int".getBytes)
		// this will print "100", as per our PutDataIntoTable example
		println(Ints.fromByteArray(v))
	}
}
