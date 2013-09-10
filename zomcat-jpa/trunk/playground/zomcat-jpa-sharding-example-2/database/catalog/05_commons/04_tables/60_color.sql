CREATE TABLE zcat_commons.color (
  c_code                  text          NOT NULL  PRIMARY KEY,

  c_created               timestamptz   NOT NULL  DEFAULT now(),
  c_created_by            text          NOT NULL,
  c_last_modified         timestamptz   NOT NULL  DEFAULT now(),
  c_last_modified_by      text          NOT NULL,
  c_flow_id               text          NULL,

  c_family_code           char(1)       NOT NULL  REFERENCES zcat_commons.color_family (cf_code),
  c_name_message_key      text          NOT NULL,
  CONSTRAINT color_code_check CHECK (c_code ~ '^[0-9]{3}$')
);

