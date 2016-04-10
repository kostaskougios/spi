-- drop

drop table pages;
drop table contributors;
drop table revisions;
drop table words;
drop sequence words_sequence;

-- create
create table pages(id bigint not null,lang varchar not null,title varchar, redirect varchar, constraint PK_PAGETEST primary key (id,lang)) SALT_BUCKETS=8;

create table contributors(revision_id bigint primary key,name varchar) SALT_BUCKETS=8;

create table revisions(id bigint primary key,PARENTID bigint,TIME timestamp,COMMENT varchar,MODEL varchar,FORMAT varchar,TEXT varchar,SHA1 varchar) SALT_BUCKETS=8;

CREATE sequence words_sequence START WITH 1 INCREMENT BY 1 CACHE 1000;

create table words(id bigint primary key,revision_id bigint ,word varchar) SALT_BUCKETS=8;

-- indexes

create index IX_Contributors_Name on Contributors(name) include(revision_id);