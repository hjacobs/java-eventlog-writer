
CREATE TABLE zcat_commons.brand (
  b_code              text         NOT NULL  PRIMARY KEY,

  b_created           timestamptz  NOT NULL  DEFAULT now(),
  b_created_by        text         NOT NULL,
  b_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  b_last_modified_by  text         NOT NULL,
  b_flow_id           text         NULL,

  b_name              text         NOT NULL,
  b_is_own_brand      boolean      NOT NULL  DEFAULT false,

  CONSTRAINT brand_code_check CHECK (b_code ~ '^[A-Z0-9]{3}$')
);

CREATE UNIQUE INDEX brand_name_uidx ON zcat_commons.brand(lower(b_name));