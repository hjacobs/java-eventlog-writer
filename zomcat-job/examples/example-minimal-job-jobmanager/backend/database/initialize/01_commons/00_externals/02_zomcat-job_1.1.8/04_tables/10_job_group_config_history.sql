CREATE TABLE zz_commons.job_group_config_history
(
  jgch_id                    SERIAL,
  jgch_type                  CHAR        NOT NULL,
  jgch_user                  TEXT        NOT NULL,
  jgch_job_group_config_id   INTEGER     NOT NULL,
  jgch_group_name            TEXT        NOT NULL,
  jgch_description           TEXT        NOT NULL,
  jgch_app_instance_keys     TEXT[],
  jgch_active                BOOLEAN     NOT NULL                 DEFAULT FALSE,
  jgch_jgc_created           TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  jgch_jgc_last_modified     TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  jgch_created               TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  PRIMARY KEY (jgch_id)
);

COMMENT ON TABLE  zz_commons.job_group_config_history
        IS 'Job Group Configuration History. Contains all alterations made to job_group_config';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_id
        IS 'Primary Key column';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_type
        IS 'Type of alteration to the respective JobGroupConfig';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_user
        IS 'User that performed the alteration to the respective JobGroupConfig';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_job_group_config_id
        IS 'Primary Key of inserted/altered Job Group Config. NULL if the original has been deleted';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_group_name
        IS 'Original Job Group Config Group Name before alteration';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_description
        IS 'Original Job Group Config Group Description before alteration';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_app_instance_keys
        IS 'Original Job Group Config AppInstance Keys before alteration';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_active
        IS 'Original Job Group Config Active State before alteration';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_jgc_created
        IS 'Original Job Group Config created date';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_jgc_last_modified
        IS 'Original Job Group Config last modified date';
COMMENT ON COLUMN zz_commons.job_group_config_history.jgch_created
        IS 'Creation date of JobGroup Config History entry - date/time of alteration';

ALTER TABLE zz_commons.job_group_config_history OWNER TO zalando;

GRANT INSERT, DELETE , UPDATE ON zz_commons.job_group_config_history TO zalando_24x7;
