CREATE TABLE zcat_option_value.heel_type
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.heel_type (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.heel_type (ov_code);
