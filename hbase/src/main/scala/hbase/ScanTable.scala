package hbase

import org.apache.hadoop.hbase.TableName

import scala.jdk.CollectionConverters._

/** run
  *
  * hbase shell
  *
  * and then
  *
  * create 'test', 'cf' put 'test', 'row1', 'cf:a', 'value1' put 'test', 'row2', 'cf:b', 'value2' put 'test', 'row3', 'cf:c', 'value3'
  *
  * @author
  *   kostas.kougios Date: 06/04/16
  */
object ScanTable extends App {
  withConnection { connection =>
    val table = connection.getTable(TableName.valueOf("test"))

    val scanner = table.getScanner("cf".getBytes)

    var r = scanner.next()
    while (r != null) {
      val m = r.getFamilyMap("cf".getBytes).asScala.map { case (k, v) =>
        (new String(k), new String(v))
      }
      val key = new String(r.getRow)
      println(s"$key -> $m")
      r = scanner.next()
    }
  }
}
