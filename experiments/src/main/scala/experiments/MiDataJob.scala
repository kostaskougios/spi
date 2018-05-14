package experiments

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.apache.spark.{SparkConf, SparkContext}

/**
  * I use this to check where I spend money by downloading midata from my bank account
  *
  * @author kostas.kougios
  */
object MiDataJob
{
	private lazy val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

	def main(args: Array[String]): Unit = {

		val conf = new SparkConf().setAppName(getClass.getName)
		val src = args(0)
		val sc = new SparkContext(conf)

		try {
			val miData = sc.textFile(src).map(_.split(",")).filter(a => a.length == 5 && a(1).trim != "Type").map {
				case Array(date, tpe, merchant, amount, balance) =>
					MiData(LocalDate.parse(date, formatter), tpe, merchant, parseMoney(amount), parseMoney(balance))
			}.cache()
			val expenses = miData.filter(_.amount < 0).cache()

			println("------------ Expenses per month ---------------")
			expenses.groupBy(_.date.getMonth).map {
				case (month, data) =>
					val biggest = data.toList.minBy(_.amount)
					(month, data.map(_.amount).sum, biggest)
			}.foreach {
				case (month, total, biggest) =>
					println(s"$month,${total.toInt},$biggest")
			}

			println("------------ Biggest expenses -----------------")
			expenses.groupBy(_.merchant).map {
				case (merchant, data) =>
					(merchant, data.map(_.amount).sum)
			}.sortBy(_._2).foreach {
				case (merchant, total) =>
					println(s"$merchant,$total")
			}
		} finally {
			sc.stop()
		}
	}

	def parseMoney(s: String): Double = {
		if (s.charAt(1) != '£') throw new IllegalArgumentException(s)
		val v = s.substring(2).toDouble
		s.charAt(0) match {
			case '-' => -v
			case '+' => v
		}
	}
}

case class MiData(date: LocalDate, tpe: String, merchant: String, amount: Double, balance: Double)