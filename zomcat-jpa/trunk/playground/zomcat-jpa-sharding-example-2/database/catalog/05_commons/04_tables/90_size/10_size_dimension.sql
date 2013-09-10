
CREATE TABLE zcat_commons.size_dimension (
  sd_code                 text         NOT NULL  PRIMARY KEY,

  sd_created              timestamptz  NOT NULL  DEFAULT now(),
  sd_created_by           text         NOT NULL,
  sd_last_modified        timestamptz  NOT NULL  DEFAULT now(),
  sd_last_modified_by     text         NOT NULL,
  sd_flow_id              text         NULL,

  sd_name                 text         NOT NULL,
  sd_display_message_key  text         NOT NULL,
  CONSTRAINT size_dimension_code_check CHECK (sd_code ~ '^[A-Z0-9]{2}$')
);

COMMENT ON TABLE zcat_commons.size_dimension IS '
Contains size dimensions. Size dimension are high level views of size charts, or in other words types of size charts.
E.g. the size dimension "3A" (textile_pants_length) can be seen as a common type for the concrete
size charts "2FC1CU2X3A", "2FHU000U3A" and "4FC1C23X3A", that describe the lengths of pants.
';

COMMENT ON COLUMN zcat_commons.size_dimension.sd_code IS '
  The code of the dimension. this code is equal to the last 2 digits of sc_code in zcat_commons.size_chart.
  zcat_commons.size_dimension_group uses this code to make aggregate all dimension of an article to a single string.
';
