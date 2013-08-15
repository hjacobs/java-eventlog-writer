CREATE TABLE zz_commons.job_scheduling_config
(
  jsc_id                         SERIAL,
  jsc_job_class                  TEXT        NOT NULL,
  jsc_cron_expression            TEXT        NOT NULL,
  jsc_description                TEXT,
  jsc_active                     BOOLEAN     NOT NULL                 DEFAULT FALSE,
  jsc_processing_limit           INTEGER     NOT NULL                 DEFAULT 0,
  jsc_startup_processing_limit   INTEGER     NOT NULL                 DEFAULT 0,
  jsc_app_instance_keys          TEXT[]      NOT NULL                 DEFAULT '{*}'::TEXT[],
  jsc_job_data                   TEXT[]      NOT NULL                 DEFAULT '{}'::TEXT[],
  jsc_job_group_config_id        INTEGER                                                     REFERENCES zz_commons.job_group_config,
  jsc_created                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  jsc_last_modified              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  PRIMARY KEY (jsc_id),
  UNIQUE (jsc_job_class, jsc_job_data)
);

COMMENT ON TABLE  zz_commons.job_scheduling_config
        IS 'Job Configuration. Configures Application specific Jobs, when, where and if they are to be executed/performed';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_id
        IS 'Primary Key column';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_job_class
        IS 'Jobs Java Classname - Fully Qualified Java Classname';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_cron_expression
        IS 'CRON Expression used for Scheduling';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_description
        IS 'Description for Job - optional';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_active
        IS 'Is Job active';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_processing_limit
        IS 'Processing Limit for Job - how many Items should be processed per Job Run';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_startup_processing_limit
        IS 'Startup Processing Limit for Job - how many Items should be processed per Job Run on Startup/in Application Maintenance Mode';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_app_instance_keys
        IS 'Allowed AppInstance Keys for Job - Job may run on any machine identified by one of the AppInstance Keys listed here';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_job_data
        IS 'Job Data - List of Key <-> Value Pairs separated by [=] character';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_job_group_config_id
        IS 'Job Group Config - if there         IS one - references [job_group_config] database table';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_created
        IS 'Created Date of Job Configuration';
COMMENT ON COLUMN zz_commons.job_scheduling_config.jsc_last_modified
        IS 'Last Modified Date of Job Configuration';

-- ALTER TABLE zz_commons.job_scheduling_config OWNER TO zalando;

GRANT INSERT, DELETE , UPDATE ON zz_commons.job_scheduling_config TO zalando_24x7;
