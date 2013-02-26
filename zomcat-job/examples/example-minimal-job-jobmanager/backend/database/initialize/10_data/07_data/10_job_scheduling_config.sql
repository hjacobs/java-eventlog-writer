

INSERT INTO zz_commons.job_scheduling_config(jsc_job_class, jsc_cron_expression, jsc_active, jsc_processing_limit, jsc_startup_processing_limit, jsc_app_instance_keys, jsc_job_data, jsc_job_group_config_id, jsc_description)
      VALUES
      -- Example Job, executes every 2 minutes
       ('de.zalando.mentoring.example.jobs.ExampleJob', '0 0/2 * * * ?', TRUE,  1000, 50, ('{'|| 'local_local' ||'}')::text[], '{}', 1, 'Example job');




