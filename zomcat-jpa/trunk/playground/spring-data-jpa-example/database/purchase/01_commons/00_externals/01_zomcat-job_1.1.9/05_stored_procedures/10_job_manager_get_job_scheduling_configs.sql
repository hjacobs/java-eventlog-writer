CREATE OR REPLACE FUNCTION zz_commons.job_manager_get_job_scheduling_configs(
  OUT result_id                           INTEGER,
  OUT result_job_class                    TEXT,
  OUT result_job_cron_expression          TEXT,
  OUT result_job_description              TEXT,
  OUT result_job_active                   BOOLEAN,
  OUT result_job_processing_limit         INTEGER,
  OUT result_job_startup_processing_limit INTEGER,
  OUT result_job_app_instance_keys        TEXT[],
  OUT result_job_data                     TEXT[],
  OUT result_job_group_name               TEXT,
  OUT result_job_group_description        TEXT,
  OUT result_job_group_active             BOOLEAN,
  OUT result_job_group_app_instance_keys  TEXT[],
  OUT result_created                      TIMESTAMP,
  OUT result_last_modified                TIMESTAMP
  )
  RETURNS setof record AS
$BODY$
-- $Id: 10_job_manager_get_job_scheduling_configs.sql 1388 2012-11-27 16:54:55Z thomas.zirke $
-- $HeadURL: https://svn.zalando.net/reboot-libs/zomcat-job/database/commons/01_commons/05_stored_procedures/10_job_manager_get_job_scheduling_configs.sql $
/**
 * Retrieve the partner config. Results are cached
 *
 * @ExpectedExecutionTime 100 ms
 * @ExpectedExecutionFrequency Daily
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from get_partner_configs();
 rollback;

 */
BEGIN

  RETURN QUERY
    SELECT jsc_id,
           jsc_job_class,
           jsc_cron_expression,
           jsc_description,
           jsc_active,
           jsc_processing_limit,
           jsc_startup_processing_limit,
           jsc_app_instance_keys,
           jsc_job_data,
           jgc_group_name,
           jgc_description,
           jgc_active,
           jgc_app_instance_keys,
           jsc_created,
           jsc_last_modified
      FROM zz_commons.job_scheduling_config
      LEFT JOIN zz_commons.job_group_config ON jgc_id = jsc_job_group_config_id
     ORDER BY jsc_job_class, jsc_job_data;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;

