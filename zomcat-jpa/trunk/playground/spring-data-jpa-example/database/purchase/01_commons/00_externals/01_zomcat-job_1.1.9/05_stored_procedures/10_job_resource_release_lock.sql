create or replace function zz_commons.job_resource_release_lock (
    p_lock_resource text,
    p_flowid text
) returns void as
$BODY$

/*
    -- $Id$
    -- $HeadURL$
*/
    delete from zz_commons.resource_lock
    where rl_resource = $1
    and rl_flowid = $2;

$BODY$
language sql
    volatile
    security definer
    cost 100;

grant execute on function zz_commons.job_resource_release_lock ( text, text ) to zalando_api_executor;
alter function zz_commons.job_resource_release_lock ( text, text ) owner to zalando;
