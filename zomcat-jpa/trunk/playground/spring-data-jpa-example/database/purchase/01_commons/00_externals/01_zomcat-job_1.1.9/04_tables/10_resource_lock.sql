create table zz_commons.resource_lock (
    rl_resource                     text        NOT NULL UNIQUE,
    rl_created                      timestamp   NOT NULL DEFAULT now(),
    rl_locked_by                    text        NOT NULL,
    rl_expected_maximum_duration    interval    NOT NULL,
    rl_flowid                       char(22)
);
