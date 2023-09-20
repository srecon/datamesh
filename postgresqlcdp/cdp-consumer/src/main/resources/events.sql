INSERT INTO public.dept (deptno,dname,loc) VALUES
	 (144,'RESEARCH1','DALLAS1');
update public.dept set dname='refresh' where deptno =144;
delete from public.dept where deptno = 144;

commit;