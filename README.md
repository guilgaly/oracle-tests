# oracle-tests

## Database :

Start database with `docker-compose up` in the `docker/` directory.

1. Connect with the "system" user to create the "test" user

- username = system
- password = Oradoc_db1
- SID = ORCLCDB

```sql
ALTER SESSION SET "_ORACLE_SCRIPT"=true;
CREATE USER test IDENTIFIED BY test;
GRANT ALL PRIVILEGES TO test;
```

2. Connect with the "test" user to create the test tables

```sql
CREATE TABLE test_tmstmp_w_tz(
    id NUMBER(10) PRIMARY KEY NOT NULL,
    tmstmp_w_tz TIMESTAMP WITH TIME ZONE NOT NULL
);
```

## JDBC driver

Download driver from [the Oracle website](https://www.oracle.com/technetwork/database/application-development/jdbc/downloads/index.html)
(version 12.2+ required for the JDBC 4.2 examples).
