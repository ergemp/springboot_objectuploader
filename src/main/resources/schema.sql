--create table in postgresql database
CREATE TABLE public."document" (
	"text" varchar NULL,
	file bytea NULL,
	file_name varchar NULL,
	content_type varchar NULL
);

-- create default schema in minio via trino
show catalogs;
show schemas from minio;
create schema minio.default;

-- archive postgresql table to minio via trino
drop table if exists minio.default.document;
create table minio.default.document as select * from postgres.public.document;

--check the data to be sure
select * from postgres.public.document;
select * from minio.default.document;