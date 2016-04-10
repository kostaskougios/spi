import sbt._

object Deps
{
	val ScalaVersion = "2.11.8"
	val PhoenixVersion = "4.5.2-HBase-1.1"

	object Scala
	{
		val Reflect = "org.scala-lang" % "scala-reflect" % ScalaVersion
		val Xml = "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
	}

	object Spark
	{
		val Version = "1.6.1"
		val Core = "org.apache.spark" %% "spark-core" % Version % "provided"
		val Streaming = "org.apache.spark" %% "spark-streaming" % Version
		val GraphX = "org.apache.spark" %% "spark-graphx" % Version % "provided"
		val Sql = "org.apache.spark" %% "spark-sql" % Version
		val Phoenix = Seq(
			"org.apache.phoenix" % "phoenix-spark" % PhoenixVersion,
			"org.apache.phoenix" % "phoenix-core" % PhoenixVersion exclude("sqlline", "sqlline")
		)

		val HBaseSpark = "com.cloudera" % "spark-hbase" % "0.0.2-clabs"
	}

	object HBase
	{
		val Version = "1.1.2"
		val Common = "org.apache.hbase" % "hbase-common" % Version
		val Client = "org.apache.hbase" % "hbase-client" % Version
		val Server = "org.apache.hbase" % "hbase-server" % Version
	}

	object Kafka
	{
		val KafkaStreaming = "org.apache.spark" %% "spark-streaming-kafka" % Spark.Version
	}

	object Libraries
	{
		val Config = "com.typesafe" % "config" % "1.3.0"
		val JodaConvert = "org.joda" % "joda-convert" % "1.7"
		val SlfLog4j = "org.slf4j" % "slf4j-log4j12" % "1.7.12"
		val ScalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
		val Mockito = "org.mockito" % "mockito-all" % "1.10.19" % "test"

		val PhoenixClient = Seq(
			"org.apache.phoenix" % "phoenix-core" % PhoenixVersion exclude("sqlline", "sqlline"),
			HBase.Client,
			HBase.Common,
			HBase.Server
		)

		val SqlLike = "org.scalikejdbc" %% "scalikejdbc" % "2.3.4"

		object Apache
		{
			val CommonsDBCP2 = "org.apache.commons" % "commons-dbcp2" % "2.1"
			val Lang3 = "org.apache.commons" % "commons-lang3" % "3.4"
			val CommonsIO = "commons-io" % "commons-io" % "2.4"
			val Pool2 = "org.apache.commons" % "commons-pool2" % "2.3"
		}

		object Play
		{
			val Json = "com.typesafe.play" %% "play-json" % "2.4.3"
		}

	}

}
