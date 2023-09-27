INSERT INTO public.dept (deptno,dname,loc) VALUES
	 (144,'RESEARCH1','DALLAS1');
update public.dept set dname='refresh' where deptno =144;
delete from public.dept where deptno = 144;

commit;


SELECT * FROM pg_replication_slots;


select pg_drop_replication_slot('demo_logical_slot_4');

// publications
Examples
Create a publication that publishes all changes in two tables:

CREATE PUBLICATION mypublication FOR TABLE users, departments;
Create a publication that publishes all changes from active departments:

CREATE PUBLICATION active_departments FOR TABLE departments WHERE (active IS TRUE);
Create a publication that publishes all changes in all tables:

CREATE PUBLICATION alltables FOR ALL TABLES;
Create a publication that only publishes INSERT operations in one table:

CREATE PUBLICATION insert_only FOR TABLE mydata
WITH (publish = 'insert');
Create a publication that publishes all changes for tables users, departments and all changes for all the tables present in the schema production:

CREATE PUBLICATION production_publication FOR TABLE users, departments, TABLES IN SCHEMA production;
Create a publication that publishes all changes for all the tables present in the schemas marketing and sales:

CREATE PUBLICATION sales_publication FOR TABLES IN SCHEMA marketing, sales;
Create a publication that publishes all changes for table users, but replicates only columns user_id and firstname:

CREATE PUBLICATION users_filtered FOR TABLE users (user_id, firstname);

====>
CREATE PUBLICATION dept_filtered FOR TABLE public.dept (deptno,dname);
CREATE PUBLICATION emp_filtered FOR TABLE public.emp (ename,job,deptno);

===>
BEGIN 779
table public.dept: INSERT: deptno[integer]:144 dname[text]:'RESEARCH1' loc[text]:'DALLAS1'
table public.dept: UPDATE: deptno[integer]:144 dname[text]:'refresh' loc[text]:'DALLAS1'
table public.dept: DELETE: deptno[integer]:144
COMMIT 779
BEGIN 780
table public.dept: INSERT: deptno[integer]:144 dname[text]:'RESEARCH1' loc[text]:'DALLAS1'
table public.dept: UPDATE: deptno[integer]:144 dname[text]:'refresh' loc[text]:'DALLAS1'
table public.dept: DELETE: deptno[integer]:144
COMMIT 780