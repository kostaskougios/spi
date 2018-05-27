import sbt._

object Deps
{
	val ScalaVersion = "2.11.12" // see mock-spark if you change scala's major version
	val PhoenixVersion = "5.0.0-alpha-HBase-2.0"

	object Scala
	{
		val Reflect = "org.scala-lang" % "scala-reflect" % ScalaVersion
		val Xml = Seq(
			"org.scala-lang.modules" %% "scala-xml" % "1.1.0",
			"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.5"
		)
	}

	object Spark
	{
		val Version = "2.3.0-hadoop-2.7.6-akt" // NOTE: modify packExcludeJars in build.sbt if you change this
	private val SparkCore = "org.apache.spark" %% "spark-core" % Version
		val Core = Seq(
			SparkCore,
			"org.apache.spark" %% "spark-yarn" % Version, // we need this to deploy to yarn
			"ch.qos.logback" % "logback-classic" % "1.2.3"
		)
		val Streaming = "org.apache.spark" %% "spark-streaming" % Version
		val GraphX = "org.apache.spark" %% "spark-graphx" % Version
		val Sql = "org.apache.spark" %% "spark-sql" % Version

		private val PhoenixExclusions = Seq(ExclusionRule(organization = "org.apache.hadoop"), ExclusionRule("sqlline", "sqlline"))
		val Phoenix = Seq(
			"org.apache.phoenix" % "phoenix-spark" % PhoenixVersion excludeAll (PhoenixExclusions: _*),
			"org.apache.phoenix" % "phoenix-core" % PhoenixVersion excludeAll (PhoenixExclusions: _*)
		)

		val AllContainedInSparkSubmit = Seq(SparkCore, Streaming, GraphX, Sql)

		val HBaseSpark = "com.cloudera" % "spark-hbase" % "0.0.2-clabs"

		// Note: there is no cassandra connector for spark 2.3.0, so I've build this PR: https://github.com/datastax/spark-cassandra-connector/pull/1175
		val CassandraConnector = "com.datastax.spark" %% "spark-cassandra-connector" % "2.3.0"
	}

	object HBase
	{
		val Version = "2.0.0"
		val Common = "org.apache.hbase" % "hbase-common" % Version
		val Client = "org.apache.hbase" % "hbase-client" % Version
		val Server = "org.apache.hbase" % "hbase-server" % Version
	}

	object Kafka
	{
		val SparkStreaming = "org.apache.spark" %% "spark-streaming-kafka-0-10" % Spark.Version
		val Clients = "org.apache.kafka" % "kafka-clients" % "1.1.0"
	}

	object Libraries
	{
		val Config = "com.typesafe" % "config" % "1.3.0"
		val Joda = "joda-time" % "joda-time" % "2.9.9"
		val JodaConvert = "org.joda" % "joda-convert" % "1.8.1"
		val ScalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
		val Mockito = "org.mockito" % "mockito-all" % "1.10.19" % "test"
		val Avro4S = "com.sksamuel.avro4s" %% "avro4s-core" % "1.8.3"

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
