# Simple examples

My big data related articles that use/explain this repository:

### Spark on hadoop

https://www.linkedin.com/pulse/spark-230-scala-hadoop-290-konstantine-kougios/

### Kafka -> Spark streaming -> Cassandra

https://www.linkedin.com/pulse/kafka-cassandra-cql-tables-via-spark-streaming-konstantine-kougios/

### Spark -> HBase (on hadoop)

https://www.linkedin.com/pulse/hbase-2-phoenix-sql-support-hadoop-276-spark-230-jobs-kougios/

### Spark-Sql, the most expensive property in my area:

https://www.linkedin.com/pulse/spark-sql-finding-most-expensive-property-my-area-konstantine-kougios/

### Avro serialization for kafka:

https://www.linkedin.com/pulse/kafka-serialization-avro-scala-konstantine-kougios/

### Performance of Avro, Parquet, ORC with Spark Sql 

https://www.linkedin.com/pulse/performance-avro-parquet-orc-spark-sql-konstantine-kougios/

# More complex applications

### Game of Life, distributed, on Spark

https://www.linkedin.com/pulse/game-life-spark-konstantine-kougios/

### Wikipedia ingestion to hdfs and hbase

See below.

# Description

This is a sample scala/spark project. It contains simple examples and also a couple of a bit more complex processing jobs.

A good spark sample is the wikipedia project. It contains code to load wikipedia exports (xml files from https://dumps.wikimedia.org/backup-index.html)
to hdfs and then process them on hadoop and export them into HBase tables (sql, phoenix).

Also don't miss the kafka examples, a kafka -> spark streaming -> cassandra pipeline.

I always try to use the latest hadoop and spark build for it. To actually build/run this project, please understand how spark jobs are submitted first as per the
instructions below in this file.

Steps to run the wikipedia code to process wikipedia xml files and copy them to hbase/phoenix tables:

- Build spark (see "BUILDING SPARK", there is a script to do it automatically) for your hadoop version. If you don't want to build spark but instead use a pre-build version, please change project/Deps.scala Spark.Version.
- Download the wikipedia exports from https://dumps.wikimedia.org/backup-index.html . The EN language is the largest export, the EL language is small and can help for quicker tests.
- The loaders project contains BreakupBigXmlFilesAndStoreToHdfs class which can be used to efficiently upload the xml files to hdfs for efficient spark processing. Run it
with the JVM args as per the class comments.
- Now you should have a /wikipedia folder in hdfs.
- Note all code can be run on a dev box too without a hadoop installation.
- Run

bin/build-project wikipedia

to build the wikipedia jars which will contain all the spark jobs for wikipedia processing.

- Inspect & run

bin/wikipedia-ingest-job

to run the 1st job that processes the wikipedia data to an easier to use format.

- If you want to populate hbase/phoenix tables, create the tables as per wikipedia/ddl.sql and then run

bin/wikipedia-populate-database-job
bin/wikipedia-words-job

Those jobs can run on the same time.

# HOW SPARK JOBS ARE SUBMITTED

Having a scala project with dependencies on top of spark is a touch task at best. Keeping the dependencies sane
and not overlapping is hard to impossible the more libraries you use. sbt dependencies can overlap with jars in
the spark/jars distro folder resulting to some very hard to debug and fix classloader issues (i.e. class
List can't be assigned to a field of type List because the classes were loaded from different spark classloaders).

There is a simple way which not only fixes the above but also allows us not to need a spark distro on dev boxes
and to also be able to use the spark version as declared in build.sbt. Spark scala projects contain all the spark
jars required for spark-submit to run. So instead of using a spark-distro, all the modules of this project are build
by exporting the dependent jars into a pseudo-distro structure (mainly a "jars" folder), and that folder is used to
both submit the task and is used to execute the task on yarn.

see bin/build-project and bin/spark folder.

# BUILDING SPARK

Examine and run bin/spark/build

Also see http://spark.apache.org/docs/latest/building-spark.html

# Compiling this project

You can run those locally within your ide or build them and execute them at your hadoop cluster. To build them for hadoop do:

bin/build-project wikipedia

This will create a fat-jar with the wikipedia code. Then:

bin/wikipedia-ingest-job

to run IngestWikipediaJob.

I submit these jobs to my home hadoop cluster (hadoop, hbase, zookeeper, kafka) of 3x virtual servers running on a 16 core opteron - 64GB RAM server.
The installation of all servers is described in my linkedin account, https://www.linkedin.com/in/kostaskougios/


Other examples include:

- sample hbase job in hbase project, FillTableJob.

- sample kafka/spark streaming code in kafka project

- sample hbase/phoenix code in phoenix / SamplePopulateJob

- spark sql (TODO)

