INSERT INTO public.dept (deptno,dname,loc) VALUES
	 (144,'RESEARCH1','DALLAS1');
update public.dept set dname='refresh' where deptno =144;
delete from public.dept where deptno = 144;

commit;


SELECT * FROM pg_replication_slots;


select pg_drop_replication_slot('demo_logical_slot_4');