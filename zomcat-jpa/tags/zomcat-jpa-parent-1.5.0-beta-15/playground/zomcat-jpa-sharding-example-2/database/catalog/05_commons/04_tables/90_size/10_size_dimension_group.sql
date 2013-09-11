
CREATE TABLE zcat_commons.size_dimension_group (
  sdg_id                serial       NOT NULL  PRIMARY KEY,

  sdg_created           timestamptz  NOT NULL  DEFAULT now(),
  sdg_created_by        text         NOT NULL,
  sdg_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  sdg_last_modified_by  text         NOT NULL,
  sdg_flow_id           text         NULL
);

COMMENT ON TABLE zcat_commons.size_dimension_group IS 'This table does only contain the IDs of the possible size
dimension groups. It was introduced to make foreign key constraints on these groups possible,
e.g. in zcat_commons.size_chart_group

Size dimension groups determine possible combinations of size dimensions, e.g. pants length charts can be combined with
pants width charts.';
