create or replace function zz_commons.job_resource_acquire_lock (
    p_locking_component             text,
    p_lock_resource                 text,
    p_flowid                        text,
    p_expected_maximum_duration     bigint
) returns boolean as
$BODY$
/**
    -- $Id$
    -- $HeadURL$
*/
begin
    insert into zz_commons.resource_lock
        (rl_resource, rl_locked_by, rl_expected_maximum_duration, rl_flowid)
    values (p_lock_resource, p_locking_component, p_expected_maximum_duration * '1 millisecond'::interval, p_flowid);

    return true; -- else an unique constraint violation is thrown.
end
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;

grant execute on function zz_commons.job_resource_acquire_lock ( text,text,text,bigint ) to zalando_api_executor;
alter function zz_commons.job_resource_acquire_lock ( text,text,text,bigint ) owner to zalando;
