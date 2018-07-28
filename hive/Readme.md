
Importing the files that were created by the benchmark on "sql" project:

    create external table impressions_orc(userId bigint, `date` timestamp, refererUrl string) stored as orc location '/tmp/big-data/impressions/orc';
    
    create external table orders_orc(userid bigint,orderno string,`date` timestamp,productid int,productcode string,producttitle string,productprice float,boughtprice float,discountpercentageapplied tinyint) stored as orc location '/tmp/big-data/orders/orc';
