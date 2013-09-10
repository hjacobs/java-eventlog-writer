create table zcat_option_value.option_value (
    ov_id                        int                        NOT NULL PRIMARY KEY,
    ov_code                      text                       NOT NULL,

    ov_created                   timestamptz                NOT NULL  DEFAULT now(),
    ov_created_by                text                       NOT NULL,
    ov_last_modified             timestamptz                NOT NULL  DEFAULT now(),
    ov_last_modified_by          text                       NOT NULL,
    ov_flow_id                   text                       NULL,

    ov_name_message_key          text                       NOT NULL,
    ov_is_active                 boolean                    NOT NULL default true,
    ov_sort_key                  int                        NOT NULL default 0
);
