
CREATE TABLE zcat_commons.size_chart_group_binding (
  scgb_size_chart_group_id  integer      NOT NULL  REFERENCES zcat_commons.size_chart_group (scg_id),
  scgb_size_chart_code      text         NOT NULL  REFERENCES zcat_commons.size_chart (sc_code),

  scgb_created              timestamptz  NOT NULL  DEFAULT now(),
  scgb_created_by           text         NOT NULL,
  scgb_last_modified        timestamptz  NOT NULL  DEFAULT now(),
  scgb_last_modified_by     text         NOT NULL,
  scgb_flow_id              text         NULL,
  PRIMARY KEY (scgb_size_chart_group_id, scgb_size_chart_code)
);

COMMENT ON TABLE  zcat_commons.size_chart_group_binding IS '
This is a join table, that assigns size charts to groups. (see also zcat_commons.size_chart_group)
';
