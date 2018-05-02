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
	//	ivyScala := ivyScala.value map {
	//		_.copy(overrideScalaVersion = true)
	//	}
)

lazy val common = project.settings(commonSettings: _*).settings(
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Core,
			Libraries.SlfLog4j,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		)
	},
	classpathConfiguration in Runtime := Configurations.CompileInternal
)
lazy val xml = project.settings(commonSettings: _*).settings(
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.Apache.CommonsIO,
			Libraries.Apache.Lang3,
			Scala.Xml
		)
	}
).dependsOn(common % "test->test;compile->compile")

lazy val sql = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Core,
			Spark.Sql,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		)
	},
	// makes sure "provided" deps are part of the runtime classpath
	classpathConfiguration in Runtime := Configurations.CompileInternal
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val phoenix = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Core,
			Spark.Sql,
			HBase.Client,
			HBase.Common,
			HBase.Server,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Phoenix
	},
	// makes sure "provided" deps are part of the runtime classpath
	classpathConfiguration in Runtime := Configurations.CompileInternal
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val hbase = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Spark.Core,
			Spark.Sql,
			HBase.Client,
			HBase.Common,
			HBase.Server,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Phoenix
	},
	// makes sure "provided" deps are part of the runtime classpath
	classpathConfiguration in Runtime := Configurations.CompileInternal
).dependsOn(common % "test->test;compile->compile")
	.enablePlugins(PackPlugin)

lazy val wikipedia = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Spark.Core,
			Spark.Sql,
			HBase.Client,
			HBase.Common,
			HBase.Server,
			Libraries.SqlLike,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		) ++ Spark.Phoenix
	},
	// makes sure "provided" deps are part of the runtime classpath
	classpathConfiguration in Runtime := Configurations.CompileInternal
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val kafka = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Spark.Core,
			Spark.Streaming,
			Kafka.KafkaStreaming,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		)
	},
	// makes sure "provided" deps are part of the runtime classpath
	classpathConfiguration in Runtime := Configurations.CompileInternal
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)

lazy val experiments = project.settings(commonSettings: _*).settings(
	//	xerial.sbt.Pack.packAutoSettings,
	libraryDependencies ++= {
		Seq(
			Libraries.ScalaTest,
			Libraries.Mockito,
			Libraries.JodaConvert,
			Spark.Core,
			Spark.Sql,
			Libraries.Apache.Lang3,
			Libraries.Apache.CommonsIO
		)
	},
	// makes sure "provided" deps are part of the runtime classpath
	classpathConfiguration in Runtime := Configurations.CompileInternal
).dependsOn(common % "test->test;compile->compile", xml)
	.enablePlugins(PackPlugin)
