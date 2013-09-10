CREATE TABLE zcat_option_value.closure
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.closure (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.closure (ov_code);
