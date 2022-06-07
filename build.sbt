import Deps._

import java.io.File

name := "spi"

ThisBuild / version := "1.0"

ThisBuild / scalaVersion := Deps.ScalaVersion

ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

ThisBuild / scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation")

ThisBuild / resolvers += Resolver.mavenLocal

ThisBuild / resolvers += "cloudera" at "https://repository.cloudera.com/artifactory/repo/"

val commonSettings = Seq(
  version := "1.0",
  excludeDependencies ++= Seq(
    // commons-logging is replaced by jcl-over-slf4j
    ExclusionRule("org.slf4j", "slf4j-log4j12")
  ),
  Test / parallelExecution := false,
  Test / fork := true,
  Test / javaOptions ++= Seq("-Xmx2G", s"-Dlogback.configurationFile=${new File("etc/ci-logback.xml").getAbsolutePath}")
)

lazy val common = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Mockito,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO,
        Libraries.ScalaGuice
      ) ++ Spark.Core
    }
  )

lazy val model = project
  .settings(commonSettings: _*)
  .settings(
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

lazy val avro = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO,
        Libraries.Avro4S
      )
    }
  )
  .dependsOn(model % "test->test;compile->compile")

lazy val xml = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Mockito,
        Libraries.Apache.CommonsIO,
        Libraries.Apache.Lang3
      ) ++ Scala.Xml
    }
  )
  .dependsOn(common % "test->test;compile->compile")

lazy val sql = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Spark.AvroDataSource,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO
      ) ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile", model % "test->test;compile->compile", xml)
  .enablePlugins(PackPlugin)

lazy val hive = project
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Spark.Hive,
        Spark.AvroDataSource,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO,
        PostGreSql.Driver
      ) ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile", model % "test->test;compile->compile", avro, xml)
  .enablePlugins(PackPlugin)

lazy val phoenix = project
  .settings(commonSettings: _*)
  .settings(
    //	xerial.sbt.Pack.packAutoSettings,
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Mockito,
        HBase.Client,
        HBase.Common,
        HBase.Server,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO
      ) ++ Spark.Phoenix ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile", xml)
  .enablePlugins(PackPlugin)

lazy val hbase = project
  .settings(commonSettings: _*)
  .settings(
    //	xerial.sbt.Pack.packAutoSettings,
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Mockito,
        HBase.Client,
        HBase.Common,
        HBase.Server,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO
      ) ++ Spark.Phoenix ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile")
  .enablePlugins(PackPlugin)

lazy val wikipedia = project
  .settings(commonSettings: _*)
  .settings(
    //	xerial.sbt.Pack.packAutoSettings,
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Mockito,
        Libraries.JodaConvert,
        HBase.Client,
        HBase.Common,
        HBase.Server,
        Libraries.SqlLike,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO
      ) ++ Spark.Phoenix ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile", xml, loaders, model % "test->test;compile->compile")
  .enablePlugins(PackPlugin)

// wait for cassandra connector for scala 2.13
//lazy val kafka = project
//  .settings(commonSettings: _*)
//  .settings(
//    //	xerial.sbt.Pack.packAutoSettings,
//    libraryDependencies ++= {
//      Seq(
//        Libraries.ScalaTest,
//        Libraries.Mockito,
//        Libraries.JodaConvert,
//        Spark.Streaming,
//        Kafka.SparkStreaming,
//        Kafka.Clients,
//        Libraries.Apache.Lang3,
//        Libraries.Apache.CommonsIO,
//        Spark.CassandraConnector
//      ) ++ Spark.Core
//    }
//  )
//  .dependsOn(common % "test->test;compile->compile", xml, model % "test->test;compile->compile", avro)
//  .enablePlugins(PackPlugin)

lazy val experiments = project
  .settings(commonSettings: _*)
  .settings(
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
  )
  .dependsOn(common % "test->test;compile->compile", xml)
  .enablePlugins(PackPlugin)

lazy val loaders = project
  .settings(commonSettings: _*)
  .settings(
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
  )
  .dependsOn(common % "test->test;compile->compile", model, xml)
  .enablePlugins(PackPlugin)

lazy val gameOfLife = project
  .settings(commonSettings: _*)
  .settings(
    //	xerial.sbt.Pack.packAutoSettings,
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO,
        Spark.Streaming,
        Kafka.SparkStreaming
      ) ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile")
  .enablePlugins(PackPlugin)

lazy val diExample = project
  .settings(commonSettings: _*)
  .settings(
    //	xerial.sbt.Pack.packAutoSettings,
    libraryDependencies ++= {
      Seq(
        Libraries.ScalaTest,
        Libraries.Apache.Lang3,
        Libraries.Apache.CommonsIO
      ) ++ Spark.Core
    }
  )
  .dependsOn(common % "test->test;compile->compile")
  .enablePlugins(PackPlugin)
