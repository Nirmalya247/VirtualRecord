# VirtualRecord
A cross-platform encrypted database management system (JAVA, C#)
It is in development stage

# Platform
It is primarily made with JAVA and another copy using C#. It also can be served as a http server on any port.

Can run on PC, Android, (Windows mobile & iOS usins Xamarin)
We will support other system in future.

It provides a array and JSON format data.

# Security
It has a highly secured file saving protocol. It uses AES encryption.

The main file is encrypted with 2 passwods (1 master password set by the programmer and a randomly generated) and all other files containing table data is randomly generated for every file.

# Syntax
Database folder has to selected when creating a object

To create new database
```
create database
```
To create table (table 1 contains no text only varchar)
```
create table 1/2 table_name
(
	column_name1 data_type size property,
	column_name2 data_type size property,
	column_name3 data_type size property,
	....
)
```
To Insert, Update, Select, delete
```
insert into table (column1,column2,column3,...) values (value1,value2,value3,...)
update table id=2 where id=1
select * from table where id=1
delete from table where id=1
```

# Supported datatyes

int, varchar, tinyint, null, datetime
