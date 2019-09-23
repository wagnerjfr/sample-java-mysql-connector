# Developing Java applications using MySQL Server Community and MySQL Connector/J
Sample of Java applications which show how to use MySQL Server Community (8.0.11) and MySQL Connector/J (8.0.11) to set up a replication topology (master ➡ slave)

## Required software
MySQL Community Server compressed TAR or ZIP version (http://dev.mysql.com/downloads/mysql/)

## Instructions
1. Clone the project
2. Import it as an existing Maven project in Eclipse or other IDE
3. Download MySQL Community Server 
4. Change variable `MYSQL_PATH` in `src/mysqlserver/Utils.java` to point to your MySQL Server path

## Applications
### Application 1
The Application1 program implements an application that:
- Initialize server instance (let's call it S1)
- Start S1 in a separate thread
- Connect to S1 *(Tip: connect to the "mysql" database, the "test" database is no longer provided)*
- Fetch the server version by executing `SELECT VERSION()` and print it to stdout
- Stop S1

### Application 2
The Application2 program implements an application that:
- Initialize a server instance (S1)
- Start S1 in a separate thread as replication master, passing the required additional options
- Initialize another server instance (S2)
- Start S2 in a separate thread as replication slave, passing the required additional options
- Set the master (S1) configuration in the slave (S2)
- Start replication in the slave (S2)
- Create a table in the master (S1)
- Wait 2 seconds (for replication to occur)
- Check that the table exists in the slave (S2)
- Stop S2 and S1

## Outputs
### Application 1
```console
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqld, --no-defaults, --initialize-insecure, --basedir=/home/wfranchi/MySQL/mysql-8.0.11, --datadir=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp1/data]
Server instance initialized!
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, ping]
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqld, --no-defaults, --port=3306, --basedir=/home/wfranchi/MySQL/mysql-8.0.11, --datadir=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp1/data, --socket=socket, --log-error=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp1/log.txt]
wait...
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, ping]
wait...
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, ping]
*****************************
MySQL Version: 8.0.11
*****************************
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, shutdown]
S1 stopped!
```

### Application 2
```console
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqld, --no-defaults, --initialize-insecure, --basedir=/home/wfranchi/MySQL/mysql-8.0.11, --datadir=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Master/data]
Server instance initialized!
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqld, --no-defaults, --initialize-insecure, --basedir=/home/wfranchi/MySQL/mysql-8.0.11, --datadir=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Slave/data]
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqld, --no-defaults, --port=3306, --basedir=/home/wfranchi/MySQL/mysql-8.0.11, --datadir=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Master/data, --socket=socket, --log-error=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Master/log.txt, --server-id=1, --log-bin=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Master/mysql-bin]
Server instance initialized!
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, ping]
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqld, --no-defaults, --port=3307, --basedir=/home/wfranchi/MySQL/mysql-8.0.11, --datadir=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Slave/data, --socket=socket, --log-error=/home/wfranchi/eclipse-oxygen-JEE/workspace/sample-java-mysql-replication/bdApp2Slave/log.txt, --server-id=2]
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3307, --user=root, ping]
wait...
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, ping]
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3307, --user=root, ping]
wait...
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, ping]
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3307, --user=root, ping]

TEST Replication

[Slave]# DESCRIBE PERSON;
Table 'mysql.PERSON' doesn't exist
[Master]# CREATE TABLE PERSON (ID INT NOT NULL, NAME VARCHAR(20), LASTNAME VARCHAR(20));

[Master]# DESCRIBE PERSON;
ID int(11) NO
NAME varchar(20) YES
LASTNAME varchar(20) YES
*****************************

[Slave]# DESCRIBE PERSON;
ID int(11) NO
NAME varchar(20) YES
LASTNAME varchar(20) YES
*****************************
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3307, --user=root, shutdown]
S2 stopped!
[/home/wfranchi/MySQL/mysql-8.0.11/bin/mysqladmin, --protocol=TCP, --port=3306, --user=root, shutdown]
S1 stopped!
```
