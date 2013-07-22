create or replace function job_proc_inf_get_job_end_time(p_job_name text)
returns timestamptz as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
declare
    l_end_time timestamptz;
BEGIN
    select jpi_end_time
    into l_end_time
    from zcat_commons.job_processing_information
    where jpi_job_name = p_job_name;

    return l_end_time;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;