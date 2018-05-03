import sbt._

object Deps
{
	val ScalaVersion = "2.11.8"
	val PhoenixVersion = "4.13.1-HBase-1.2"

	object Scala
	{
		val Reflect = "org.scala-lang" % "scala-reflect" % ScalaVersion
		val Xml = "org.scala-lang.modules" %% "scala-xml" % "1.1.0"
	}

	object Spark
	{
		val Version = "2.3.0"
		val Core = Seq(
			"org.apache.spark" %% "spark-core" % Version % "provided",
			"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.5"
		) ++ Hadoop.All
		val Streaming = "org.apache.spark" %% "spark-streaming" % Version
		val GraphX = "org.apache.spark" %% "spark-graphx" % Version % "provided"
		val Sql = "org.apache.spark" %% "spark-sql" % Version
		val Phoenix = Seq(
			"org.apache.phoenix" % "phoenix-spark" % PhoenixVersion,
			"org.apache.phoenix" % "phoenix-core" % PhoenixVersion exclude("sqlline", "sqlline")
		)

		val HBaseSpark = "com.cloudera" % "spark-hbase" % "0.0.2-clabs"
	}

	object Hadoop
	{
		private val Version = "2.9.0"
		val All = Seq(
			"org.apache.hadoop" % "hadoop-annotations" % Version,
			"org.apache.hadoop" % "hadoop-auth" % Version,
			"org.apache.hadoop" % "hadoop-client" % Version,
			"org.apache.hadoop" % "hadoop-common" % Version,
			"org.apache.hadoop" % "hadoop-hdfs-client" % Version,
			"org.apache.hadoop" % "hadoop-mapreduce-client-app" % Version,
			"org.apache.hadoop" % "hadoop-mapreduce-client-common" % Version,
			"org.apache.hadoop" % "hadoop-mapreduce-client-core" % Version,
			"org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % Version,
			"org.apache.hadoop" % "hadoop-mapreduce-client-shuffle" % Version,
			"org.apache.hadoop" % "hadoop-yarn-api" % Version,
			"org.apache.hadoop" % "hadoop-yarn-client" % Version,
			"org.apache.hadoop" % "hadoop-yarn-common" % Version,
			"org.apache.hadoop" % "hadoop-yarn-server-common" % Version,
			"org.apache.hadoop" % "hadoop-yarn-server-nodemanager" % Version,
			"org.apache.hadoop" % "hadoop-hdfs" % Version
		)
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
		val KafkaStreaming = "org.apache.spark" %% "spark-streaming-kafka-0-10" % Spark.Version
	}

	object Libraries
	{
		val Config = "com.typesafe" % "config" % "1.3.0"
		val JodaConvert = "org.joda" % "joda-convert" % "1.7"
		val SlfLog4j = "org.slf4j" % "slf4j-log4j12" % "1.7.12"
		val ScalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
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
			val Lang3 = "org.apache.commons" % "commons-lang3" % "3.7"
			val CommonsIO = "commons-io" % "commons-io" % "2.6"
			val Pool2 = "org.apache.commons" % "commons-pool2" % "2.5.0"
		}

	}

}
