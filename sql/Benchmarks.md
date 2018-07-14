# Performance of Avro, Parquet, ORC with Spark Sql

In this example we will compare performance of different spark sql formats when running a number of analytics queries on
them. We'll compare these formats:

Avro: a compact serialization format, https://avro.apache.org/

Parquet: a columnar storage format , https://parquet.apache.org/

ORC: an other columnar storage format, https://orc.apache.org/

We will create 2 tables, one simulating page impressions (PageImpression class) and one for orders (Order class). The first 
one has 3 columns only where as the 2nd one has 9 and simulates a denormalized table:

    case class PageImpression(
    	userId: Long,
    	date: Timestamp,
    	refererUrl: String
    )
    
    case class Order(
    	userId: Long,
    	orderNo: String,
    	date: Timestamp,
    	productId: Int,
    	productCode: String,
    	productTitle: String,
    	productPrice: Float,
    	boughtPrice: Float,
    	discountPercentageApplied: Byte
    )

The tests were run using spark 2.3.1 on hadoop 2.7 on a 2 x 8-core Opteron 4386 with HDFS been stored in 4x 7200rpm disks.
4 spark executors with 4 cores each to run the queries and simulate shuffle overheads that would occur on a bigger cluster. 
Each executor had 4GB of RAM available.
    
Note: because the data are random (with a bit of care taken to simulate actual data), real life data might give different
results.

## Data file sizes

2 billion rows for page impressions and orders were created. The sizes for impressions are:

    21.2 G  avro
    15.1 G  orc
    30.2 G  parquet

The sizes for orders are:

    124.2 G  avro
    93.4 G   orc
    103.3 G  parquet

We notice that the ORC format is the most compact (at least with the default settings). Ofcourse all formats can be
configured to i.e. compress the data even more (by default the data are compressed) but for this test we'll use the
default configured values.

## Results
Now we will run a number of queries against the data on all file formats. Time is measured (in milliseconds) for each query
and the results are here:

## Impressions

    +---------------------------------------------------------------------------------------------------------------------------+------+-------+-------+
    |Query                                                                                                                      |  Avro|Parquet|    ORC|
    +---------------------------------------------------------------------------------------------------------------------------+------+-------+-------+
    |                                                                            select * from impressions_* where userId=500000| 20122|   7233|   2442|
    |            select count(userId) as c,max(date),min(date),userId from impressions_* group by userId order by c desc limit 5|218442| 211114|  93826|
    |                                select count(userId) as c,userId from impressions_* group by userId order by c desc limit 5|199100| 116684|  81840|
    |                                                                                         select count(*) from impressions_*|112249|    638|    430|
    |                                                                           select count(distinct userId) from impressions_*|164674|  93136|  56973|
    |                                                                                        select min(date) from impressions_*|116480|  71117|   4360|
    |                                                                                        select max(date) from impressions_*|113089|  69316|   5258|
    |select count(distinct userId) from impressions_* where date between '2010-02-01T00:00:00.00Z' and '2010-03-01T00:00:00.00Z'|113821|1118782|1068352|
    |select count(distinct userId) from impressions_* where date between '2010-08-01T00:00:00.00Z' and '2010-09-01T00:00:00.00Z'|115589|1121685|1085033|
    +---------------------------------------------------------------------------------------------------------------------------+------+-------+-------+

## Orders

    +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+------+-------+-------+
    |Query                                                                                                                                                                                                              |  Avro|Parquet|    ORC|
    +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+------+-------+-------+
    |                                                                                                                            select sum(boughtPrice) s,userId from orders_* group by userId order by s desc limit 10|864091| 167213| 172766|
    |select productCode,productTitle,max(discountPercentageApplied) d from orders_* where date between '2010-01-01T00:00:00.00Z' and '2010-03-01T00:00:00.00Z' group by productCode,productTitle order by d desc limit 5|833974|1054963|1030633|
    |select productCode,productTitle,max(discountPercentageApplied) d from orders_* where date between '2010-03-01T00:00:00.00Z' and '2010-06-01T00:00:00.00Z' group by productCode,productTitle order by d desc limit 5|775505|1056606|1026863|
    |                                                                           select productCode,productTitle,max(discountPercentageApplied) d from orders_* group by productCode,productTitle order by d desc limit 5|788296| 122440| 120402|
    |                                                                                                                                                                         select * from orders_* where userId=500000| 28083|  24160|   6788|
    |                                                                                                                                                                          select * from orders_* order by productId|788798| 670418| 690997|
    +-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+------+-------+-------+

## Outcome of the benchmark

As we can see, at least with the default settings, the ORC format gives best performance and smallest file sizes. Also
the columnar formats (ORC, Parquet) overall perform better, in some cases avro has good performance, especially if all
data in all columns have to be read for the query. ORC was the fastest while creating the data too. ORC's light weight
indexes also play nicely with date's when those are incremental, as it is common on apps that gather information real 
time.

Surprisingly the avro format is not the fastest one or the one with the smallest file sizes during ingestion. It might 
be due to the overheads introduced by spark sql or the library spark-avro or the snappy compression. It should be the 
fastest because it doesn't have to organize the data in any way.

Surely this is not a comprehensive benchmark but maybe indicative of what we can expect. Please let me know if you 
would like to have a particular query added to the tests.

# How the benchmark is implemented

Package com.aktit.sql.performance contains classes to generate random DataFrames (CreateRandomData) and run benchmarks 
on those via BenchmarkImpressions and BenchmarkOrders classes. Care is taken so that the userId and date fields to have
reasonable values.
