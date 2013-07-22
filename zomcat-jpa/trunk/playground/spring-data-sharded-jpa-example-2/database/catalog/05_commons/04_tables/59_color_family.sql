CREATE TABLE zcat_commons.color_family (
  cf_code                  char(1)       NOT NULL  PRIMARY KEY,

  cf_created               timestamptz   NOT NULL  DEFAULT now(),
  cf_created_by            text          NOT NULL,
  cf_last_modified         timestamptz   NOT NULL  DEFAULT now(),
  cf_last_modified_by      text          NOT NULL,
  cf_flow_id               text          NULL,

  cf_name_message_key      text          NOT NULL
);


COMMENT ON TABLE zcat_commons.color_family IS 'Contains the 19 Zalando colors, that define family for
                                               zcat_commons.color. The code is used for config sku generation.';
