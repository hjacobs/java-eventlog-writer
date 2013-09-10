
CREATE TABLE zcat_commons.size_chart_group (
  scg_id                serial       NOT NULL  PRIMARY KEY,

  scg_created           timestamptz  NOT NULL  DEFAULT now(),
  scg_created_by        text         NOT NULL,
  scg_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  scg_last_modified_by  text         NOT NULL,
  scg_flow_id           text         NULL,

  scg_dimension_group_id  integer      NOT NULL  REFERENCES zcat_commons.size_dimension_group (sdg_id)
);

COMMENT ON TABLE  zcat_commons.size_chart_group IS '
Size chart groups are combinations of size charts, that can be used to define an article model. Size chart groups
contain at least one size chart (single size chart, e.g. for T-Shirts; multi size charts,
e.g. for Jeans [width x length]). Furthermore a size chart group belongs to a size dimension group, that defines,
which charts can be combined and in which order.

This table only contains a list of all group ids; in zcat_commons.size_chart_group_binding the size charts are
linked to the groups.
';
