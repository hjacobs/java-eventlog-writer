create or replace function job_proc_inf_create_or_update_job_end_time(p_job_name text, p_end_time timestamptz)
returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Updates job entry in table with new time or creates new entry is it doesn't exists.
 *
 * @ExpectedExecutionTime 20ms
 * @ExpectedExecutionFrequency EveryMinute
 */
/* --testing

  begin;
    -- set search_path to zcat_api_r12_00_43, public;

    select job_proc_inf_create_or_update_job_end_time('test', now());

  rollback;
*/
BEGIN
    update zcat_commons.job_processing_information
       set jpi_end_time = p_end_time
     where jpi_job_name = p_job_name;

    if not found then
      insert into zcat_commons.job_processing_information(jpi_job_name, jpi_end_time)
           values (p_job_name, p_end_time);
    end if;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;