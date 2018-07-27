
Importing the files that were created by the benchmark on "sql" project:

    create external table impressions_orc(userId bigint, `date` timestamp, refererUrl string) stored as orc location '/tmp/big-data/impressions/orc';
