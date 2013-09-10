
CREATE TABLE zcat_commons.size_chart (
  sc_code                       text                       NOT NULL  PRIMARY KEY,

  sc_created                    timestamptz                NOT NULL  DEFAULT now(),
  sc_created_by                 text                       NOT NULL,
  sc_last_modified              timestamptz                NOT NULL  DEFAULT now(),
  sc_last_modified_by           text                       NOT NULL,
  sc_flow_id                    text                       NULL,

  sc_dimension_code             text                       NOT NULL  REFERENCES zcat_commons.size_dimension (sd_code),
  sc_brand_code                 text                       NULL      REFERENCES zcat_commons.brand (b_code),
  sc_description_message_key    text                       NOT NULL,

  CONSTRAINT size_chart_code_check CHECK (sc_code ~ '^[1-7][FMKUA][A-Z0-9]{2}[A-Z0-9]{3}[AEFIUSX][A-Z0-9]{2}$'),
  CONSTRAINT size_chart_dimension_code_check CHECK (sc_dimension_code = substring(sc_code FROM 9 FOR 2)),
  CONSTRAINT size_chart_brand_code_check1 CHECK (substring(sc_code FROM 5 FOR 3) <> '000' OR sc_brand_code IS NULL),
  CONSTRAINT size_chart_brand_code_check2 CHECK (substring(sc_code FROM 5 FOR 3) = sc_brand_code OR sc_brand_code IS NULL)
);

COMMENT ON TABLE  zcat_commons.size_chart IS '
This table contains size charts. A size chart group a couple of sizes (see also zcat_commons.size).
Our size charts can be brand dependent. Each size chart belongs to a size dimension.';
