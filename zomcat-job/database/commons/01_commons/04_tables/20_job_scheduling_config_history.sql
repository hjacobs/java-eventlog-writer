CREATE TABLE zz_commons.job_scheduling_config_history
(
  jsch_id                         SERIAL,
  jsch_type                       CHAR        NOT NULL,
  jsch_user                       TEXT        NOT NULL,
  jsch_job_scheduling_config_id   INTEGER     NOT NULL,
  jsch_job_class                  TEXT        NOT NULL,
  jsch_cron_expression            TEXT        NOT NULL,
  jsch_description                TEXT,
  jsch_active                     BOOLEAN     NOT NULL                 DEFAULT FALSE,
  jsch_processing_limit           INTEGER     NOT NULL                 DEFAULT 0,
  jsch_startup_processing_limit   INTEGER     NOT NULL                 DEFAULT 0,
  jsch_app_instance_keys          TEXT[]      NOT NULL                 DEFAULT '{*}'::TEXT[],
  jsch_job_data                   TEXT[]      NOT NULL                 DEFAULT '{}'::TEXT[],
  jsch_job_group_config_id        INTEGER,
  jsch_jsc_created                TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  jsch_jsc_last_modified          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  jsch_created                    TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  PRIMARY KEY (jsch_id)
);

COMMENT ON TABLE  zz_commons.job_scheduling_config_history
        IS 'Job Configuration. Configures Application specific Jobs, when, where and if they are to be executed/performed';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_id
        IS 'Primary Key column';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_type
        IS 'History Entry Type. D - Deleted - contains old Data, U - Update - contains old Data, I - Insert - contains new Data';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_user
        IS 'User having made the change that created the History Entry';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_job_class
        IS 'Jobs Java Classname - Fully Qualified Java Classname';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_cron_expression
        IS 'CRON Expression used for Scheduling';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_description
        IS 'Description for Job - optional';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_active
        IS 'Is Job active';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_processing_limit
        IS 'Processing Limit for Job - how many Items should be processed per Job Run';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_startup_processing_limit
        IS 'Startup Processing Limit for Job - how many Items should be processed per Job Run on Startup/in Application Maintenance Mode';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_app_instance_keys
        IS 'Allowed AppInstance Keys for Job - Job may run on any machine identified by one of the AppInstance Keys listed here';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_job_data
        IS 'Job Data - List of Key <-> Value Pairs separated by [=] character';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_job_group_config_id
        IS 'Job Group Config - if there         IS one - references [job_group_config] database table';
COMMENT ON COLUMN zz_commons.job_scheduling_config_history.jsch_created
        IS 'Created Date of Job Configuration';

-- ALTER TABLE zz_commons.job_scheduling_config_history OWNER TO zalando;

GRANT INSERT, DELETE, UPDATE ON zz_commons.job_scheduling_config_history TO zalando_24x7;
