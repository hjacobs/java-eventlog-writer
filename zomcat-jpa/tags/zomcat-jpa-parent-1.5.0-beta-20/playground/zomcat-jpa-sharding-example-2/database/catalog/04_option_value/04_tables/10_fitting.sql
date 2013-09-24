CREATE TABLE zcat_option_value.fitting
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.fitting (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.fitting (ov_code);
