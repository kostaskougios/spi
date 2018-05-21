import Deps._

name := "spi"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := Deps.ScalaVersion

javacOptions in ThisBuild ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions in ThisBuild ++= Seq("-target:jvm-1.7", "-unchecked", "-feature", "-deprecation")

resolvers in ThisBuild += Resolver.mavenLocal

resolvers in ThisBuild += "neo4j" at "http://m2.neo4j.org/content/repositories/releases/"

resolvers in ThisBuild += "cloudera" at "https://repository.cloudera.com/artifactory/repo/"

val commonSettings = Seq(
	version := "1.0",
	excludeDependencies ++= Seq(
		// commons-logging is replaced by jcl-over-slf4j
		ExclusionRule("org.slf4j", "slf4j-log4j12")
	)
)

lazy val common = project.settings(commonSettings: _*).settings(
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Core
	}
)

lazy val model = project.settings(commonSettings: _*).settings(
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO,
			Libraries.Joda,
			Libraries.JodaConvert
		) ++ Scala.Xml
	}
)

lazy val avro = project.settings(commonSettings: _*).settings(
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO,
			Libraries.Avro4S
		)
	}
).dependsOn(model % "test->test;compile->compile")

lazy val xml = project.settings(commonSettings: _*).settings(
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.Apache.CommonsIO,
			Libraries.Apache.Lang3
		) ++ Scala.Xml
	}
).dependsOn(common % "test->test;compile->compile")

lazy val sql = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Sql,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val phoenix = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Sql,
			HBase.Client,
			HBase.Common,
			HBase.Server,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Phoenix ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val hbase = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Sql,
			HBase.Client,
			HBase.Common,
			HBase.Server,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Phoenix ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile")
	.enablePlugins(PackPlugin)

lazy val wikipedia = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Spark.Sql,
			HBase.Client,
			HBase.Common,
			HBase.Server,
			Libraries.SqlLike,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Phoenix ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile", xml, loaders, model)
	.enablePlugins(PackPlugin)

lazy val kafka = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Spark.Streaming,
			Kafka.SparkStreaming,
			Kafka.Clients,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO,
			Spark.CassandraConnector,
			Spark.Sql
		) ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile", xml, model)
	.enablePlugins(PackPlugin)

lazy val experiments = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Spark.Sql,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val loaders = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Core
	}
).dependsOn(common % "test->test;compile->compile", model, xml)
	.enablePlugins(PackPlugin)