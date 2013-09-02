  CREATE TABLE zcat_option_value.target_group_age
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.target_group_age (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.target_group_age (ov_code);
