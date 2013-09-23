CREATE TABLE zz_commons.job_group_config
(
  jgc_id                    SERIAL,
  jgc_group_name            TEXT        NOT NULL,
  jgc_description           TEXT        NOT NULL,
  jgc_app_instance_keys     TEXT[],
  jgc_active                BOOLEAN     NOT NULL                 DEFAULT FALSE,
  jgc_created               TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  jgc_last_modified         TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
  PRIMARY KEY (jgc_id),
  UNIQUE (jgc_group_name)
);

COMMENT ON TABLE  zz_commons.job_group_config
        IS 'Job Group Configurations. Jobs may be grouped in JobGroups. JobGroups can be (de)activated globally - one flag - many jobs';
COMMENT ON COLUMN zz_commons.job_group_config.jgc_id
        IS 'Primary Key column';
COMMENT ON COLUMN zz_commons.job_group_config.jgc_group_name
        IS 'JobGroup Name - Unique Name for JobGroup(s)';
COMMENT ON COLUMN zz_commons.job_group_config.jgc_app_instance_keys
        IS 'Allowed AppInstanceKeys for JobGroup - all Jobs in this JobGroup may run on given AppInstances. Jobs can override the allowed AppInstances in its JobConfiguration';
COMMENT ON COLUMN zz_commons.job_group_config.jgc_active
        IS 'Is JobGroup active? If inactive - NO Jobs in the JobGroup will be performed';
COMMENT ON COLUMN zz_commons.job_group_config.jgc_created
        IS 'Created Timestamp of JobGroup';
COMMENT ON COLUMN zz_commons.job_group_config.jgc_last_modified
        IS 'Last Modified Timestamp of JobGroup';

-- ALTER TABLE zz_commons.job_group_config OWNER TO zalando;

GRANT INSERT, DELETE , UPDATE ON zz_commons.job_group_config TO zalando_24x7;
