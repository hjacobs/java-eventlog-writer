CREATE TABLE zcat_option_value.toe_cap
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.toe_cap (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.toe_cap (ov_code);
