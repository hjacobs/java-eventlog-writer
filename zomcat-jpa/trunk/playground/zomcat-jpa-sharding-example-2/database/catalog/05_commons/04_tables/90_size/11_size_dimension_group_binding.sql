
CREATE TABLE zcat_commons.size_dimension_group_binding (
  sdgb_code              text         NOT NULL  REFERENCES zcat_commons.size_dimension (sd_code),
  sdgb_group_id          integer      NOT NULL  REFERENCES zcat_commons.size_dimension_group (sdg_id),

  sdgb_created           timestamptz  NOT NULL  DEFAULT now(),
  sdgb_created_by        text         NOT NULL,
  sdgb_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  sdgb_last_modified_by  text         NOT NULL,
  sdgb_flow_id           text         NULL,

  sdgb_position          integer      NOT NULL,
  sdgb_prefix_id         integer      NULL      REFERENCES zcat_commons.size_dimension_prefix (sdp_id),

  PRIMARY KEY (sdgb_code, sdgb_group_id),

  CONSTRAINT size_dimension_group_binding_position_check CHECK (sdgb_position > 0),
  CONSTRAINT size_dimension_group_binding_prefix_check   CHECK (sdgb_position > 1 OR sdgb_prefix_id IS NULL)
);

COMMENT ON TABLE zcat_commons.size_dimension_group_binding IS '
  Defines valid combinations of dimensions of an article and their positions within an aggregated string.
  beside its function to declare which dimension combinations are valid it is used to calculate
  a string representation of a multidimensional article, which can be displayed in the shop.

  Example:

    pants have a 2-dimensional size. the string representation should be <length>x<width>.
    dimension code for length: 0A
    dimension code for width : 0B

    so the dimension group would be defined by two rows in this table:
    code | group | position | prefix
    --------------------------------
    0A   | 1     | 1        | null
    0B   | 1     | 2        |  "x"
';

COMMENT ON COLUMN zcat_commons.size_dimension_group_binding.sdgb_group_id IS '
All rows with the same group id define a valid combination of dimensions, ie the size dimension group.
';

COMMENT ON COLUMN zcat_commons.size_dimension_group_binding.sdgb_position IS '
The position of the dimension value within its group. (starts with 1).
';

COMMENT ON COLUMN zcat_commons.size_dimension_group_binding.sdgb_prefix_id IS '
 the prefix or separator which is used to separate dimension in the aggregated string representation.
';

