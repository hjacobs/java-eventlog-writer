CREATE TABLE zcat_option_value.availability
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.availability (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.availability (ov_code);