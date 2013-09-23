CREATE OR replace FUNCTION zz_commons.job_resource_acquire_lock (
    p_locking_component             text,
    p_lock_resource                 text,
    p_flowid                        text,
    p_expected_maximum_duration     bigint
) RETURNS boolean AS
$BODY$
/**
    -- $Id$
    -- $HeadURL$
*/
BEGIN
    BEGIN
        IF ( SELECT count(1) FROM zz_commons.resource_lock WHERE rl_resource = p_lock_resource ) > 0 THEN
            RETURN false;
        END IF;

        INSERT INTO zz_commons.resource_lock
            (rl_resource, rl_locked_by, rl_expected_maximum_duration, rl_flowid)
        VALUES (p_lock_resource, p_locking_component, p_expected_maximum_duration * '1 millisecond'::interval, p_flowid);

        RETURN true; -- else an unique constraint violation is thrown.

    EXCEPTION
        WHEN unique_violation THEN
            RETURN false;
    END;
END
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;

GRANT EXECUTE ON FUNCTION zz_commons.job_resource_acquire_lock (text, text, text, bigint) TO zalando_api_executor;
-- ALTER FUNCTION zz_commons.job_resource_acquire_lock (text, text, text, bigint) owner TO zalando;
