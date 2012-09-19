create or replace function zz_commons.job_resource_peek_lock (
    p_lock_resource                 text
) returns boolean as
$BODY$
/**
    -- $Id$
    -- $HeadURL$
*/
begin
    perform 1
    from zz_commons.resource_lock
    where rl_resource = p_lock_resource;

    return found;

end
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
