create or replace function zz_commons.job_resource_release_lock (
    p_lock_resource text
) returns void as
$BODY$

/*
    -- $Id$
    -- $HeadURL$
*/
    delete from zz_commons.resource_lock
    where rl_resource = $1;

$BODY$
language sql
    volatile
    security definer
    cost 100;
