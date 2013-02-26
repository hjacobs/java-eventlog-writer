

INSERT INTO zz_commons.job_scheduling_config(jsc_job_class, jsc_cron_expression, jsc_active, jsc_processing_limit, jsc_startup_processing_limit, jsc_app_instance_keys, jsc_job_data, jsc_job_group_config_id, jsc_description)
      VALUES
      -- Example Job, executes every 10 minutes (xx:00, xx:10, xx:20 ,,,)
       ('de.zalando.mentoring.example.jobs.ExampleJob', '0 0/10 * * * ?', TRUE,  100, 5, ('{'|| 'local_local' ||'}')::text[], '{}', 1, 'Example job');




