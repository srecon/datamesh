# Datamesh
The idea behind the projet is to implements a few tools and technics for replicating data from source to data lake using the datamesh architecture principles.

According to the ```self-service data platform``` principle, the approch is to utilize the PostgreSQL logical replication technichs for replicating data from the source to the target.

1. Project ```postgresqlcdp``` is my sandbox to study the PostgreSQL CDP mechanism.
2. Project ```pgEasyReplication-fork``` is a forked project, already implemented the basic of the PostgreSQL CDC. With this project you can get all the DML events in JSON format. THe projet is avaiable here https://github.com/davyam/pgEasyReplication 

Steps:

1. Add or change the following paramters in file postgresql.conf

```
listen_addresses = '*'		# listen all network interfaces
max_wal_senders = 4		# max number of walsender processes
wal_keep_size = 4		# in logfile segments, 16MB each; 0 disables
wal_level = logical             # minimal, replica, or logical
max_replication_slots = 4       # max number of replication slots
```

2. Enable the following user to connect to the database in file pg_hba.conf

```
local   replication   all                   trust
host    replication   all   192.168.32.0/24 md5
host    replication   all   ::1/128         md5

```

3. Create two tables as shown below

```
create table dept(
  deptno integer,
  dname  text,
  loc    text,
  constraint pk_dept primary key (deptno)
);

create table emp(
  empno    integer,
  ename    text,
  job      text,
  mgr      integer,
  hiredate date,
  sal      integer,
  comm     integer,
  deptno   integer,
  constraint pk_emp primary key (empno),
  constraint fk_deptno foreign key (deptno) references dept (deptno)
);
```

CREATE UNIQUE INDEX ename_idx ON emp (ename);

4. Inserts some data

```
insert into dept
values(10, 'ACCOUNTING', 'NEW YORK');
insert into dept
values(20, 'RESEARCH', 'DALLAS');
insert into dept
values(30, 'SALES', 'CHICAGO');
insert into dept
values(40, 'OPERATIONS', 'BOSTON');

insert into emp
values(
 7839, 'KING', 'PRESIDENT', null,
 to_date('17-11-1981','dd-mm-yyyy'),
 5000, null, 10
);
insert into emp
values(
 7698, 'BLAKE', 'MANAGER', 7839,
 to_date('1-5-1981','dd-mm-yyyy'),
 2850, null, 30
);
insert into emp
values(
 7782, 'CLARK', 'MANAGER', 7839,
 to_date('9-6-1981','dd-mm-yyyy'),
 2450, null, 10
);
insert into emp
values(
 7566, 'JONES', 'MANAGER', 7839,
 to_date('2-4-1981','dd-mm-yyyy'),
 2975, null, 20
);
insert into emp
values(
 7788, 'SCOTT', 'ANALYST', 7566,
 to_date('13-07-87','dd-mm-rr') - 85,
 3000, null, 20
);
insert into emp
values(
 7902, 'FORD', 'ANALYST', 7566,
 to_date('3-12-1981','dd-mm-yyyy'),
 3000, null, 20
);
insert into emp
values(
 7369, 'SMITH', 'CLERK', 7902,
 to_date('17-12-1980','dd-mm-yyyy'),
 800, null, 20
);
insert into emp
values(
 7499, 'ALLEN', 'SALESMAN', 7698,
 to_date('20-2-1981','dd-mm-yyyy'),
 1600, 300, 30
);
insert into emp
values(
 7521, 'WARD', 'SALESMAN', 7698,
 to_date('22-2-1981','dd-mm-yyyy'),
 1250, 500, 30
);
insert into emp
values(
 7654, 'MARTIN', 'SALESMAN', 7698,
 to_date('28-9-1981','dd-mm-yyyy'),
 1250, 1400, 30
);
insert into emp
values(
 7844, 'TURNER', 'SALESMAN', 7698,
 to_date('8-9-1981','dd-mm-yyyy'),
 1500, 0, 30
);
insert into emp
values(
 7876, 'ADAMS', 'CLERK', 7788,
 to_date('13-07-87', 'dd-mm-rr') - 51,
 1100, null, 20
);
insert into emp
values(
 7900, 'JAMES', 'CLERK', 7698,
 to_date('3-12-1981','dd-mm-yyyy'),
 950, null, 30
);
insert into emp
values(
 7934, 'MILLER', 'CLERK', 7782,
 to_date('23-1-1982','dd-mm-yyyy'),
 1300, null, 10
);


commit;
```

5. Create Publication for the tables as follows

```
CREATE PUBLICATION cidade_pub FOR TABLE public.emp (ename,job,deptno);
```
Note that, we just create a publication only with 3 fields of the EMP table.

It's a new fetures on PostgreSQL v 15 for iltering columns on table

6. Change all the database connectivity params and run the Java application named App.Java